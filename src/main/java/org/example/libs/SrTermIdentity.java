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

        byte flags = buf.get();
        String flagBits = String.format("%8s", Integer.toBinaryString(flags & 0xFF)).replace(' ', '0');

        // В Go младший бит последний, в Java так же (читаем слева направо)
        MNE = flagBits.substring(0, 1);
        BSE = flagBits.substring(1, 2);
        NIDE = flagBits.substring(2, 3);
        SSRA = flagBits.substring(3, 4);
        LNGCE = flagBits.substring(4, 5);
        IMSIE = flagBits.substring(5, 6);
        IMEIE = flagBits.substring(6, 7);
        HDIDE = flagBits.substring(7, 8);

        if ("1".equals(HDIDE)) {
            homeDispatcherIdentifier = Short.toUnsignedInt(buf.getShort());
        }

        if ("1".equals(IMEIE)) {
            byte[] imeiBytes = new byte[15];
            buf.get(imeiBytes);
            IMEI = new String(imeiBytes, StandardCharsets.US_ASCII);
        }

        if ("1".equals(IMSIE)) {
            byte[] imsiBytes = new byte[16];
            buf.get(imsiBytes);
            IMSI = new String(imsiBytes, StandardCharsets.US_ASCII);
        }

        if ("1".equals(LNGCE)) {
            byte[] langBytes = new byte[3];
            buf.get(langBytes);
            languageCode = new String(langBytes, StandardCharsets.US_ASCII);
        }

        if ("1".equals(NIDE)) {
            networkIdentifier = new byte[3];
            buf.get(networkIdentifier);
        } else {
            networkIdentifier = new byte[0];
        }

        if ("1".equals(BSE)) {
            bufferSize = Short.toUnsignedInt(buf.getShort());
        }

        if ("1".equals(MNE)) {
            byte[] mobileBytes = new byte[15];
            buf.get(mobileBytes);
            mobileNumber = new String(mobileBytes, StandardCharsets.US_ASCII);
        }
    }

    @Override
    public byte[] encode() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        ByteBuffer buf = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
        buf.putInt((int) terminalIdentifier);
        output.write(buf.array());

        String flagStr = MNE + BSE + NIDE + SSRA + LNGCE + IMSIE + IMEIE + HDIDE;
        int flags = Integer.parseInt(flagStr, 2);
        output.write(flags);

        if ("1".equals(HDIDE)) {
            buf = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN);
            buf.putShort((short) homeDispatcherIdentifier);
            output.write(buf.array());
        }

        if ("1".equals(IMEIE)) {
            byte[] imeiBytes = new byte[15];
            byte[] rawImei = IMEI.getBytes(StandardCharsets.US_ASCII);
            System.arraycopy(rawImei, 0, imeiBytes, 0, Math.min(rawImei.length, 15));
            output.write(imeiBytes);
        }

        if ("1".equals(IMSIE)) {
            byte[] imsiBytes = new byte[16];
            byte[] rawImsi = IMSI.getBytes(StandardCharsets.US_ASCII);
            System.arraycopy(rawImsi, 0, imsiBytes, 0, Math.min(rawImsi.length, 16));
            output.write(imsiBytes);
        }

        if ("1".equals(LNGCE)) {
            byte[] langBytes = new byte[3];
            byte[] rawLang = languageCode.getBytes(StandardCharsets.US_ASCII);
            System.arraycopy(rawLang, 0, langBytes, 0, Math.min(rawLang.length, 3));
            output.write(langBytes);
        }

        if ("1".equals(NIDE)) {
            if (networkIdentifier.length != 3)
                throw new IOException("networkIdentifier length must be 3 when NIDE flag is set");
            output.write(networkIdentifier);
        }

        if ("1".equals(BSE)) {
            buf = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN);
            buf.putShort((short) bufferSize);
            output.write(buf.array());
        }

        if ("1".equals(MNE)) {
            byte[] mobileBytes = new byte[15];
            byte[] rawMobile = mobileNumber.getBytes(StandardCharsets.US_ASCII);
            System.arraycopy(rawMobile, 0, mobileBytes, 0, Math.min(rawMobile.length, 15));
            output.write(mobileBytes);
        }

        return output.toByteArray();
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
