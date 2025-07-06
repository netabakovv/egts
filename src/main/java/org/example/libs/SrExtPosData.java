package org.example.libs;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.BitSet;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SrExtPosData implements BinaryData{
    private String navigationSystemFieldExists = "0"; // NSFE
    private String satellitesFieldExists = "0";       // SFE
    private String pdopFieldExists = "0";             // PFE
    private String hdopFieldExists = "0";             // HFE
    private String vdopFieldExists = "0";             // VFE

    private int verticalDilutionOfPrecision;   // VDOP
    private int horizontalDilutionOfPrecision; // HDOP
    private int positionDilutionOfPrecision;   // PDOP
    private byte satellites;                   // SAT
    private int navigationSystem;              // NS

    /**
     * Декодирует байты в структуру SrExtPosData.
     */
    @Override
    public void decode(byte[] content) throws IOException{
        if (content == null || content.length < 1) {
            throw new IllegalArgumentException("Недостаточно данных для декодирования SrExtPosData");
        }

        ByteBuffer buf = ByteBuffer.wrap(content);
        byte flags = buf.get();

        BitSet flagBits = BitSet.valueOf(new byte[]{flags});

        this.vdopFieldExists = flagBits.get(7) ? "1" : "0";
        this.hdopFieldExists = flagBits.get(6) ? "1" : "0";
        this.pdopFieldExists = flagBits.get(5) ? "1" : "0";
        this.satellitesFieldExists = flagBits.get(4) ? "1" : "0";
        this.navigationSystemFieldExists = flagBits.get(3) ? "1" : "0";

        byte[] tmpBuf = new byte[2];

        if ("1".equals(vdopFieldExists)) {
            if (buf.remaining() < 2)
                throw new IllegalArgumentException("Недостаточно данных для чтения VDOP");

            buf.get(tmpBuf);
            this.verticalDilutionOfPrecision = Short.toUnsignedInt(ByteBuffer.wrap(tmpBuf).order(java.nio.ByteOrder.LITTLE_ENDIAN).getShort());
        }

        if ("1".equals(hdopFieldExists)) {
            if (buf.remaining() < 2)
                throw new IllegalArgumentException("Недостаточно данных для чтения HDOP");

            buf.get(tmpBuf);
            this.horizontalDilutionOfPrecision = Short.toUnsignedInt(ByteBuffer.wrap(tmpBuf).order(java.nio.ByteOrder.LITTLE_ENDIAN).getShort());
        }

        if ("1".equals(pdopFieldExists)) {
            if (buf.remaining() < 2)
                throw new IllegalArgumentException("Недостаточно данных для чтения PDOP");

            buf.get(tmpBuf);
            this.positionDilutionOfPrecision = Short.toUnsignedInt(ByteBuffer.wrap(tmpBuf).order(java.nio.ByteOrder.LITTLE_ENDIAN).getShort());
        }

        if ("1".equals(satellitesFieldExists)) {
            if (buf.remaining() < 1)
                throw new IllegalArgumentException("Недостаточно данных для чтения SAT");

            this.satellites = buf.get();
        }

        if ("1".equals(navigationSystemFieldExists)) {
            if (buf.remaining() < 2)
                throw new IllegalArgumentException("Недостаточно данных для чтения NS");

            buf.get(tmpBuf);
            this.navigationSystem = Short.toUnsignedInt(ByteBuffer.wrap(tmpBuf).order(java.nio.ByteOrder.LITTLE_ENDIAN).getShort());
        }
    }

    /**
     * Кодирует структуру в массив байт.
     */
    @Override
    public byte[] encode() throws IOException {
        StringBuilder flagBuilder = new StringBuilder();
        flagBuilder.append(vdopFieldExists)
                .append(hdopFieldExists)
                .append(pdopFieldExists)
                .append(satellitesFieldExists)
                .append(navigationSystemFieldExists)
                .append('0')
                .append('0')
                .append('0');


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
                ("1".equals(vdopFieldExists) ? 2 : 0) +
                ("1".equals(hdopFieldExists) ? 2 : 0) +
                ("1".equals(pdopFieldExists) ? 2 : 0) +
                ("1".equals(satellitesFieldExists) ? 1 : 0) +
                ("1".equals(navigationSystemFieldExists) ? 2 : 0);

        ByteBuffer buf = ByteBuffer.allocate(requiredSize);

        buf.put((byte) flags);

        if ("1".equals(vdopFieldExists)) {
            buf.putShort(Short.reverseBytes((short) verticalDilutionOfPrecision));
        }

        if ("1".equals(hdopFieldExists)) {
            buf.putShort(Short.reverseBytes((short) horizontalDilutionOfPrecision));
        }

        if ("1".equals(pdopFieldExists)) {
            buf.putShort(Short.reverseBytes((short) positionDilutionOfPrecision));
        }

        if ("1".equals(satellitesFieldExists)) {
            buf.put(satellites);
        }

        if ("1".equals(navigationSystemFieldExists)) {
            buf.putShort(Short.reverseBytes((short) navigationSystem));
        }

        byte[] result = new byte[buf.position()];
        buf.rewind();
        buf.get(result);

        return result;
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
