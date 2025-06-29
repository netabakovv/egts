package org.example.libs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * SrPassengersCountersData — EGTS_SR_PASSENGERS_COUNTERS
 * Подзапись сервиса EGTS_TELEDATA_SERVICE.
 * Используется АСН для передачи на аппаратно-программный комплекс данных о показаниях счетчиков пассажиропотока.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SrPassengersCountersData {
    private String rawDataFlag = "0";               // RawDataFlag — бит 0 флага
    private String doorsPresented = "00000000";     // Doors Presented — байт
    private String doorsReleased = "00000000";      // Doors Released — байт
    private int moduleAddress;                      // Module Address — USHORT, Little Endian
    private List<PassengersCounter> passengersCountersData = new ArrayList<>();
    private byte[] passengersCountersRawData = new byte[0];

    /**
     * Декодирует байты в структуру SrPassengersCountersData.
     */
    public void decode(byte[] content) throws Exception {
        if (content == null || content.length < 5) {
            throw new IllegalArgumentException("Недостаточно данных для декодирования SrPassengersCountersData");
        }

        ByteBuffer buf = ByteBuffer.wrap(content);

        // Чтение первого байта флагов
        byte flagsByte = buf.get();
        BitSet flagsBits = BitSet.valueOf(new byte[]{flagsByte});
        this.rawDataFlag = flagsBits.get(7) ? "1" : "0";

        // Чтение DoorsPresented (байт)
        byte dprByte = buf.get();
        this.doorsPresented = String.format("%8s", Integer.toBinaryString(dprByte & 0xFF)).replace(' ', '0');

        // Чтение DoorsReleased (байт)
        byte drlByte = buf.get();
        this.doorsReleased = String.format("%8s", Integer.toBinaryString(drlByte & 0xFF)).replace(' ', '0');

        // Чтение ModuleAddress (2 байта, Little Endian)
        if (buf.remaining() < 2) {
            throw new IllegalArgumentException("Недостаточно данных для чтения ModuleAddress");
        }
        this.moduleAddress = Short.toUnsignedInt(buf.getShort());

        // Обработка данных о пассажирах
        if ("0".equals(rawDataFlag)) {
            passengersCountersData.clear();

            for (int i = 1; i <= 8; i++) {
                if (doorsPresented.charAt(8 - i) == '0') {
                    continue;
                }

                if (buf.remaining() < 2) {
                    throw new IllegalArgumentException("Недостаточно данных для чтения In/Out двери #" + i);
                }

                byte in = buf.get();
                byte out = buf.get();

                passengersCountersData.add(new PassengersCounter((byte) i, in, out));
            }
        } else {
            byte[] raw = new byte[buf.remaining()];
            buf.get(raw);
            this.passengersCountersRawData = raw;
        }
    }

    /**
     * Кодирует структуру в массив байт.
     */
    public byte[] encode() throws Exception {
        ByteBuffer buf = ByteBuffer.allocate(1024); // достаточно большой буфер

        // Флаги
        StringBuilder flagBuilder = new StringBuilder();
        for (int i = 0; i < 7; i++) {
            flagBuilder.append('0');
        }
        flagBuilder.append(rawDataFlag);
        String flagStr = flagBuilder.toString();

        if (flagStr.length() != 8) {
            throw new IllegalArgumentException("Флаг должен быть длиной 8 символов");
        }

        for (int i = 0; i < flagStr.length(); i++) {
            char c = flagStr.charAt(i);
            if (c != '0' && c != '1') {
                throw new IllegalArgumentException("Флаг содержит недопустимый символ на позиции " + i + ": '" + c + "'");
            }
        }

        buf.put((byte) Integer.parseInt(flagStr, 2));

        // DoorsPresented
        if (doorsPresented.length() != 8) {
            throw new IllegalArgumentException("DoorsPresented должно содержать 8 бит");
        }
        buf.put((byte) Integer.parseInt(doorsPresented, 2));

        // DoorsReleased
        if (doorsReleased.length() != 8) {
            throw new IllegalArgumentException("DoorsReleased должно содержать 8 бит");
        }
        buf.put((byte) Integer.parseInt(doorsReleased, 2));

        // ModuleAddress
        buf.putShort(Short.reverseBytes((short) moduleAddress));

        // Данные о пассажирах
        if ("0".equals(rawDataFlag)) {
            for (PassengersCounter counter : passengersCountersData) {
                byte[] encoded = counter.encode();
                buf.put(encoded);
            }
        } else {
            buf.put(passengersCountersRawData);
        }

        byte[] result = new byte[buf.position()];
        buf.rewind();
        buf.get(result);

        return result;
    }

    /**
     * Возвращает длину закодированной подзаписи.
     */
    public int length() {
        try {
            return encode().length;
        } catch (Exception e) {
            return 0;
        }
    }
}
