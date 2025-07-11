package org.example.libs;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

@Data
public class SrTermIdentity implements BinaryData {

    private long terminalIdentifier;

    private String MNE = "0";
    private String BSE = "0";
    private String NIDE = "0";
    private String SSRA = "0";
    private String LNGCE = "0";
    private String IMSIE = "0";
    private String IMEIE = "0";
    private String HDIDE = "0";

    private int homeDispatcherIdentifier;
    private String IMEI = "";
    private String IMSI = "";
    private String languageCode = "";
    private byte[] networkIdentifier = new byte[0];
    private int bufferSize;
    private String mobileNumber = "";

    @Override
    public void decode(byte[] data) throws IOException {
        ByteBuffer buf = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);

        terminalIdentifier = Integer.toUnsignedLong(buf.getInt());
        System.out.println(">> [SrTermIdentity] Прочитан terminalIdentifier = " + terminalIdentifier);


        byte flags = buf.get();
        MNE = isBitSet(flags, 7);
        BSE = isBitSet(flags, 6);
        NIDE = isBitSet(flags, 5);
        SSRA = isBitSet(flags, 4);
        LNGCE = isBitSet(flags, 3);
        IMSIE = isBitSet(flags, 2);
        IMEIE = isBitSet(flags, 1);
        HDIDE = isBitSet(flags, 0);

        if ("1".equals(HDIDE)) homeDispatcherIdentifier = Short.toUnsignedInt(buf.getShort());
        if ("1".equals(IMEIE)) IMEI = readString(buf, 15);
        if ("1".equals(IMSIE)) IMSI = readString(buf, 16);
        if ("1".equals(LNGCE)) languageCode = readString(buf, 3);
        if ("1".equals(NIDE)) {
            networkIdentifier = new byte[3];
            buf.get(networkIdentifier);
        }
        if ("1".equals(BSE)) bufferSize = Short.toUnsignedInt(buf.getShort());
        if ("1".equals(MNE)) mobileNumber = readString(buf, 15);
    }

    @Override
    public byte[] encode() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        output.write(ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt((int) terminalIdentifier).array());

        byte flags = 0;
        flags |= bit(MNE, 7);
        flags |= bit(BSE, 6);
        flags |= bit(NIDE, 5);
        flags |= bit(SSRA, 4);
        flags |= bit(LNGCE, 3);
        flags |= bit(IMSIE, 2);
        flags |= bit(IMEIE, 1);
        flags |= bit(HDIDE, 0);
        output.write(flags);

        if ("1".equals(HDIDE)) output.write(shortToBytes(homeDispatcherIdentifier));
        if ("1".equals(IMEIE)) output.write(fixedLengthBytes(IMEI, 15));
        if ("1".equals(IMSIE)) output.write(fixedLengthBytes(IMSI, 16));
        if ("1".equals(LNGCE)) output.write(fixedLengthBytes(languageCode, 3));
        if ("1".equals(NIDE)) {
            if (networkIdentifier.length != 3)
                throw new IOException("networkIdentifier must be 3 bytes if NIDE == 1");
            output.write(networkIdentifier);
        }
        if ("1".equals(BSE)) output.write(shortToBytes(bufferSize));
        if ("1".equals(MNE)) output.write(fixedLengthBytes(mobileNumber, 15));

        return output.toByteArray();
    }

    // Вспомогательные методы:
    private String isBitSet(byte flags, int pos) {
        return ((flags >> pos) & 0x01) == 1 ? "1" : "0";
    }

    private byte bit(String flag, int pos) {
        return "1".equals(flag) ? (byte) (1 << pos) : 0;
    }

    private byte[] shortToBytes(int value) {
        return ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort((short) value).array();
    }

    private byte[] fixedLengthBytes(String input, int length) {
        byte[] result = new byte[length];
        byte[] src = input.getBytes(StandardCharsets.US_ASCII);
        System.arraycopy(src, 0, result, 0, Math.min(src.length, length));
        return result;
    }

    private String readString(ByteBuffer buf, int len) {
        byte[] arr = new byte[len];
        buf.get(arr);
        return new String(arr, StandardCharsets.US_ASCII);
    }


    @Override
    public int length() {
        try {
            return encode().length;
        } catch (Exception e) {
            return 0;
        }
    }
}
