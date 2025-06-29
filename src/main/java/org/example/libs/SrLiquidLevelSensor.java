package org.example.libs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.nio.ByteBuffer;
import java.util.BitSet;

/**
 * SrLiquidLevelSensor — EGTS_SR_LIQUID_LEVEL_SENSOR
 * Структура подзаписи для передачи данных о показаниях датчика уровня жидкости (ДУЖ)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SrLiquidLevelSensor {
    // Битовые флаги (биты 7-0 в первом байте)
    @ToString.Exclude
    private String liquidLevelSensorErrorFlag = "0"; // LLSEF — Bit 6
    @ToString.Exclude
    private String liquidLevelSensorValueUnit = "00"; // LLSVU — Bits 5-4
    @ToString.Exclude
    private String rawDataFlag = "0";                // RDF — Bit 3
    @ToString.Exclude
    private int liquidLevelSensorNumber;              // LLSN — Bits 2-0

    // Основные поля
    private int moduleAddress;            // MADDR — 2 байта, Little Endian
    private long liquidLevelSensorData;   // LLSD — 4 байта, Little Endian

    /**
     * Декодирует байты в структуру SrLiquidLevelSensor.
     */
    public void decode(byte[] content) throws Exception {
        if (content == null || content.length < 7) {
            throw new IllegalArgumentException("Недостаточно данных для декодирования SrLiquidLevelSensor");
        }

        ByteBuffer buf = ByteBuffer.wrap(content);

        byte flags = buf.get();

        BitSet flagBits = BitSet.valueOf(new byte[]{flags});

        this.liquidLevelSensorErrorFlag = flagBits.get(6) ? "1" : "0";
        this.rawDataFlag = flagBits.get(3) ? "1" : "0";

        // LLSVU — биты 5 и 4
        this.liquidLevelSensorValueUnit = (flagBits.get(5) ? "1" : "0") + (flagBits.get(4) ? "1" : "0");

        // LLSN — биты 2, 1, 0
        int llsn = 0;
        for (int i = 0; i < 3; i++) {
            if (flagBits.get(i)) {
                llsn |= (1 << i);
            }
        }
        this.liquidLevelSensorNumber = llsn;

        // Чтение адреса модуля (2 байта)
        if (buf.remaining() < 2) {
            throw new IllegalArgumentException("Недостаточно данных для чтения MADDR");
        }
        this.moduleAddress = Short.toUnsignedInt(buf.getShort());

        // Чтение данных датчика (4 байта)
        if (buf.remaining() < 4) {
            throw new IllegalArgumentException("Недостаточно данных для чтения LLSD");
        }
        this.liquidLevelSensorData = Integer.toUnsignedLong(buf.getInt());
    }

    /**
     * Кодирует структуру в массив байт.
     */
    public byte[] encode() throws Exception {
        StringBuilder flagBuilder = new StringBuilder();
        flagBuilder.append('0') // Бит 7 — зарезервирован
                .append(liquidLevelSensorErrorFlag)
                .append(liquidLevelSensorValueUnit.charAt(0))
                .append(liquidLevelSensorValueUnit.charAt(1))
                .append(rawDataFlag)
                .append(String.format("%3s", Integer.toBinaryString(liquidLevelSensorNumber & 0x07)).replace(' ', '0'));

        String flagStr = flagBuilder.toString();

        // Проверка флагов
        if (flagStr.length() != 8) {
            throw new IllegalArgumentException("Флаг должен быть длиной 8 символов");
        }

        for (int i = 0; i < flagStr.length(); i++) {
            char c = flagStr.charAt(i);
            if (c != '0' && c != '1') {
                throw new IllegalArgumentException("Флаг содержит недопустимый символ на позиции " + i + ": '" + c + "'");
            }
        }

        int flags;
        try {
            flags = Integer.parseInt(flagStr, 2);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Не удалось преобразовать флаг в число", e);
        }

        int requiredSize = 1 + // флаг
                ("1".equals(liquidLevelSensorErrorFlag) ? 2 : 0) +
                ("1".equals(rawDataFlag) ? 2 : 0) +
                ("1".equals(liquidLevelSensorValueUnit) ? 2 : 0) +
                2 + // MADDR
                4;  // LLSD

        ByteBuffer buf = ByteBuffer.allocate(requiredSize);

        // Запись флага
        buf.put((byte) flags);

        // Запись адреса модуля
        buf.order(java.nio.ByteOrder.LITTLE_ENDIAN);
        buf.putShort((short) moduleAddress);

        // Запись данных датчика
        buf.putInt((int) liquidLevelSensorData);

        return buf.array();
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