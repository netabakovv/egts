package org.example.libs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.BitSet;

/**
 * SrLiquidLevelSensor — EGTS_SR_LIQUID_LEVEL_SENSOR
 * Структура подзаписи для передачи данных о показаниях датчика уровня жидкости (ДУЖ)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SrLiquidLevelSensor implements BinaryData{
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
    @Override
    public void decode(byte[] content) throws IOException {
        if (content == null || content.length < 7) {
            throw new IllegalArgumentException("Недостаточно данных для декодирования SrLiquidLevelSensor");
        }

        ByteBuffer buf = ByteBuffer.wrap(content);
        buf.order(java.nio.ByteOrder.LITTLE_ENDIAN);  // Ставим порядок сразу

        byte flags = buf.get();

        // Разбор битовых флагов через маски и сдвиги
        this.liquidLevelSensorErrorFlag = ((flags >> 6) & 1) == 1 ? "1" : "0";
        this.rawDataFlag = ((flags >> 3) & 1) == 1 ? "1" : "0";

        int llvu = (flags >> 4) & 0b11;  // биты 5-4
        this.liquidLevelSensorValueUnit = String.format("%2s", Integer.toBinaryString(llvu)).replace(' ', '0');

        this.liquidLevelSensorNumber = flags & 0b111; // биты 2-0

        if (buf.remaining() < 2) {
            throw new IllegalArgumentException("Недостаточно данных для чтения MADDR");
        }
        this.moduleAddress = Short.toUnsignedInt(buf.getShort());

        if (buf.remaining() < 4) {
            throw new IllegalArgumentException("Недостаточно данных для чтения LLSD");
        }
        this.liquidLevelSensorData = Integer.toUnsignedLong(buf.getInt());
    }

    /**
     * Кодирует структуру в массив байт.
     */
    @Override
    public byte[] encode() throws IOException {
        // Формируем байт флагов через битовые операции
        int flags = 0;
        flags |= (liquidLevelSensorNumber & 0b111); // биты 0-2

        if ("1".equals(rawDataFlag)) {
            flags |= (1 << 3);
        }

        // liquidLevelSensorValueUnit - строка из 2 символов '0' или '1'
        if (liquidLevelSensorValueUnit == null || liquidLevelSensorValueUnit.length() != 2
                || !liquidLevelSensorValueUnit.matches("[01]{2}")) {
            throw new IllegalArgumentException("LLSVU должен быть строкой из двух бит (например, \"00\", \"10\")");
        }
        int llvu = Integer.parseInt(liquidLevelSensorValueUnit, 2);
        flags |= (llvu << 4); // биты 4-5

        if ("1".equals(liquidLevelSensorErrorFlag)) {
            flags |= (1 << 6);
        }

        // бит 7 — зарезервирован (0)

        ByteBuffer buf = ByteBuffer.allocate(7);
        buf.order(java.nio.ByteOrder.LITTLE_ENDIAN);  // Ставим порядок сразу

        buf.put((byte) flags);
        buf.putShort((short) moduleAddress);
        buf.putInt((int) liquidLevelSensorData);

        return buf.array();
    }

    /**
     * Возвращает длину закодированной подзаписи.
     */
    @Override
    public int length() {
        try {
            return encode().length;
        } catch (Exception e) {
            return 0;
        }
    }
}