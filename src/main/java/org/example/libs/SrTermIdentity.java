package org.example.libs;

import lombok.Getter;
import lombok.Setter;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

@Getter
@Setter
public class SrTermIdentity implements BinaryData {
    private long terminalIdentifier;

    private boolean MNE;
    private boolean BSE;
    private boolean NIDE;
    private boolean SSRA;
    private boolean LNGCE;
    private boolean IMSIE;
    private boolean IMEIE;
    private boolean HDIDE;

    private int homeDispatcherIdentifier;
    private String IMEI;
    private String IMSI;
    private String languageCode;
    private byte[] networkIdentifier;
    private int bufferSize;
    private String mobileNumber;

    @Override
    public void decode(byte[] data) throws Exception {
        ByteBuffer buf = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);

        terminalIdentifier = Integer.toUnsignedLong(buf.getInt());

        byte flags = buf.get();
        MNE = ((flags >> 7) & 0x01) == 1;
        BSE = ((flags >> 6) & 0x01) == 1;
        NIDE = ((flags >> 5) & 0x01) == 1;
        SSRA = ((flags >> 4) & 0x01) == 1;
        LNGCE = ((flags >> 3) & 0x01) == 1;
        IMSIE = ((flags >> 2) & 0x01) == 1;
        IMEIE = ((flags >> 1) & 0x01) == 1;
        HDIDE = (flags & 0x01) == 1;

        if (HDIDE) {
            homeDispatcherIdentifier = Short.toUnsignedInt(buf.getShort());
        }

        if (IMEIE) {
            byte[] imeiBytes = new byte[15];
            buf.get(imeiBytes);
            IMEI = new String(imeiBytes).trim();
        }

        if (IMSIE) {
            byte[] imsiBytes = new byte[16];
            buf.get(imsiBytes);
            IMSI = new String(imsiBytes).trim();
        }

        if (LNGCE) {
            byte[] langBytes = new byte[3];
            buf.get(langBytes);
            languageCode = new String(langBytes).trim();
        }

        if (NIDE) {
            networkIdentifier = new byte[3];
            buf.get(networkIdentifier);
        }

        if (BSE) {
            bufferSize = Short.toUnsignedInt(buf.getShort());
        }

        if (MNE) {
            byte[] mobileBytes = new byte[15];
            buf.get(mobileBytes);
            mobileNumber = new String(mobileBytes).trim();
        }
    }

    @Override
    public byte[] encode() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        ByteBuffer buf = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
        buf.putInt(Math.toIntExact(terminalIdentifier));
        output.write(buf.array());

        byte flags = 0;
        flags |= (MNE ? 1 : 0) << 7;
        flags |= (BSE ? 1 : 0) << 6;
        flags |= (NIDE ? 1 : 0) << 5;
        flags |= (SSRA ? 1 : 0) << 4;
        flags |= (LNGCE ? 1 : 0) << 3;
        flags |= (IMSIE ? 1 : 0) << 2;
        flags |= (IMEIE ? 1 : 0) << 1;
        flags |= (HDIDE ? 1 : 0);
        output.write(flags);

        if (HDIDE) {
            buf = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN);
            buf.putShort((short) homeDispatcherIdentifier);
            output.write(buf.array());
        }

        if (IMEIE) {
            byte[] imeiBytes = Arrays.copyOf(IMEI.getBytes(), 15);
            output.write(imeiBytes);
        }

        if (IMSIE) {
            byte[] imsiBytes = Arrays.copyOf(IMSI.getBytes(), 16);
            output.write(imsiBytes);
        }

        if (LNGCE) {
            byte[] langBytes = Arrays.copyOf(languageCode.getBytes(), 3);
            output.write(langBytes);
        }

        if (NIDE) {
            output.write(networkIdentifier);
        }

        if (BSE) {
            buf = ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN);
            buf.putShort((short) bufferSize);
            output.write(buf.array());
        }

        if (MNE) {
            byte[] mobileBytes = Arrays.copyOf(mobileNumber.getBytes(), 15);
            output.write(mobileBytes);
        }

        return output.toByteArray();
    }

    @Override
    public short length() {
        try {
            return (short) encode().length;
        } catch (Exception e) {
            return 0;
        }
    }
}
