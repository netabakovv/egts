package org.example.libs;

import lombok.Data;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Data
public class SrPosData implements BinaryData {
    private ZonedDateTime navigationTime;
    private double latitude;
    private double longitude;

    private String alte;
    private String lohs;
    private String lahs;
    private String mv;
    private String bb;
    private String cs;
    private String fix;
    private String vld;

    private byte directionHighestBit;
    private int altitudeSign;
    private int speed;         // 14 бит, 0–16383
    private byte direction;    // 7 бит (8 с флагом)

    private int odometer;      // 24 бита
    private byte digitalInputs;
    private byte source;
    private int altitude;      // 24 бита

    @Override
    public void decode(byte[] data) {
        ByteBuffer buf = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);

        // 1. Navigation time
        long secondsSince2010 = Integer.toUnsignedLong(buf.getInt());
        navigationTime = ZonedDateTime.ofInstant(
                Instant.ofEpochSecond(secondsSince2010 + 1262304000L),
                ZoneOffset.UTC
        );

        // 2. Latitude
        long rawLat = Integer.toUnsignedLong(buf.getInt());
        latitude = (double) rawLat * 90 / 0xFFFFFFFFL;

        // 3. Longitude
        long rawLong = Integer.toUnsignedLong(buf.getInt());
        longitude = (double) rawLong * 180 / 0xFFFFFFFFL;

        // 4. Flags (byte -> 8 флагов)
        byte flags = buf.get();
        String bits = String.format("%8s", Integer.toBinaryString(flags & 0xFF)).replace(' ', '0');
        alte = bits.substring(0, 1);
        lohs = bits.substring(1, 2);
        lahs = bits.substring(2, 3);
        mv   = bits.substring(3, 4);
        bb   = bits.substring(4, 5);
        cs   = bits.substring(5, 6);
        fix  = bits.substring(6, 7);
        vld  = bits.substring(7);

        // 5. Speed with embedded bits
        int spdBits = Short.toUnsignedInt(buf.getShort());
        directionHighestBit = (byte) ((spdBits >> 15) & 0x1);
        altitudeSign = (byte) ((spdBits >> 14) & 0x1);
        speed = (spdBits & 0x3FFF) / 10;

        // 6. Direction (7 бит + старший из spdBits)
        byte dirByte = buf.get();
        direction = (byte) ((dirByte & 0x7F) | (directionHighestBit << 7));

        // 7. Odometer (3 байта)
        byte[] odoBuf = new byte[]{buf.get(), buf.get(), buf.get(), 0x00};
        odometer = ByteBuffer.wrap(odoBuf).order(ByteOrder.LITTLE_ENDIAN).getInt();

        // 8. Digital inputs
        digitalInputs = buf.get();

        // 9. Source
        source = buf.get();

        // 10. Altitude (если ALTE == "1")
        if ("1".equals(alte)) {
            byte[] altBuf = new byte[]{buf.get(), buf.get(), buf.get(), 0x00};
            altitude = ByteBuffer.wrap(altBuf).order(ByteOrder.LITTLE_ENDIAN).getInt();
        }
    }

    @Override
    public byte[] encode() {
        ByteBuffer buf = ByteBuffer.allocate(32).order(ByteOrder.LITTLE_ENDIAN);

        // 1. Navigation time
        long secondsSince2010 = navigationTime.toEpochSecond() - 1262304000L;
        buf.putInt((int) secondsSince2010);

        // 2. Latitude
        long latVal = (long)((latitude / 90.0) * 0xFFFFFFFFL);
        // Раскладываем на 4 байта в Little Endian
        buf.put((byte)(latVal & 0xFF));
        buf.put((byte)((latVal >> 8) & 0xFF));
        buf.put((byte)((latVal >> 16) & 0xFF));
        buf.put((byte)((latVal >> 24) & 0xFF));

        // 3. Longitude
        long lonVal = (long)((longitude / 180.0) * 0xFFFFFFFFL);
        buf.put((byte)(lonVal & 0xFF));
        buf.put((byte)((lonVal >> 8) & 0xFF));
        buf.put((byte)((lonVal >> 16) & 0xFF));
        buf.put((byte)((lonVal >> 24) & 0xFF));


        // 4. Flags
        String flagBits = alte + lohs + lahs + mv + bb + cs + fix + vld;
        byte flags = (byte) Integer.parseInt(flagBits, 2);
        buf.put(flags);

        // 5. Speed + bits
        int spdEnc = (speed * 10) & 0x3FFF;
        spdEnc |= (altitudeSign & 0x1) << 14;
        spdEnc |= (directionHighestBit & 0x1) << 15;
        buf.putShort((short) spdEnc);

        // 6. Direction (без старшего бита)
        buf.put((byte) (direction & 0x7F));

        // 7. Odometer (3 байта)
        buf.put((byte) (odometer & 0xFF));
        buf.put((byte) ((odometer >> 8) & 0xFF));
        buf.put((byte) ((odometer >> 16) & 0xFF));

        // 8. Digital Inputs
        buf.put(digitalInputs);

        // 9. Source
        buf.put(source);

        // 10. Altitude
        if ("1".equals(alte)) {
            buf.put((byte) (altitude & 0xFF));
            buf.put((byte) ((altitude >> 8) & 0xFF));
            buf.put((byte) ((altitude >> 16) & 0xFF));
        }

        // Return only written bytes
        byte[] out = new byte[buf.position()];
        buf.rewind();
        buf.get(out);
        return out;
    }

    @Override
    public int length() {
        try {
            return encode().length;
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public String toString() {
        return "SrPosData{" +
                "navigationTime=" + navigationTime +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", alte='" + alte + '\'' +
                ", lohs='" + lohs + '\'' +
                ", lahs='" + lahs + '\'' +
                ", mv='" + mv + '\'' +
                ", bb='" + bb + '\'' +
                ", cs='" + cs + '\'' +
                ", fix='" + fix + '\'' +
                ", vld='" + vld + '\'' +
                ", directionHighestBit=" + directionHighestBit +
                ", altitudeSign=" + altitudeSign +
                ", speed=" + speed +
                ", direction=" + (direction & 0xFF) +
                ", odometer=" + odometer +
                ", digitalInputs=" + (digitalInputs & 0xFF) +
                ", source=" + (source & 0xFF) +
                ", altitude=" + altitude +
                '}';
    }
}
