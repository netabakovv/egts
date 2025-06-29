package org.example.libs;

import lombok.Getter;
import lombok.Setter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Getter
@Setter
public class SrPosData implements BinaryData {
    private LocalDateTime navigationTime;
    private double latitude;
    private double longitude;

    private boolean ALTE, LOHS, LAHS, MV, BB, CS, FIX, VLD;
    private int altitudeSign;
    private int speed;
    private byte direction; // младшие 7 бит
    private long odometer;
    private byte digitalInputs;
    private byte source;
    private long altitude;

    private static final LocalDateTime START_DATE = LocalDateTime.of(2010, 1, 1, 0, 0);

    @Override
    public void decode(byte[] data) throws Exception {
        ByteBuffer buf = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);

        long seconds = Integer.toUnsignedLong(buf.getInt());
        navigationTime = START_DATE.plusSeconds(seconds);

        long rawLat = Integer.toUnsignedLong(buf.getInt());
        latitude = (double) rawLat * 90 / 0xFFFFFFFFL;

        long rawLon = Integer.toUnsignedLong(buf.getInt());
        longitude = (double) rawLon * 180 / 0xFFFFFFFFL;

        byte flags = buf.get();
        ALTE = ((flags >> 7) & 1) == 1;
        LOHS = ((flags >> 6) & 1) == 1;
        LAHS = ((flags >> 5) & 1) == 1;
        MV   = ((flags >> 4) & 1) == 1;
        BB   = ((flags >> 3) & 1) == 1;
        CS   = ((flags >> 2) & 1) == 1;
        FIX  = ((flags >> 1) & 1) == 1;
        VLD  = (flags & 1) == 1;

        int spdBits = Short.toUnsignedInt(buf.getShort());
        int directionHighestBit = (spdBits >> 15) & 1;
        altitudeSign = (spdBits >> 14) & 1;
        speed = (spdBits & 0x3FFF) / 10;

        byte dirByte = buf.get();
        direction = (byte) (dirByte & 0x7F); // сохраняем только младшие 7 бит
        if (directionHighestBit == 1) {
            direction |= (1 << 7);
        }

        byte[] odoBytes = new byte[4];
        odoBytes[0] = buf.get();
        odoBytes[1] = buf.get();
        odoBytes[2] = buf.get();
        odoBytes[3] = 0x00;
        odometer = Integer.toUnsignedLong(ByteBuffer.wrap(odoBytes).order(ByteOrder.LITTLE_ENDIAN).getInt());

        digitalInputs = buf.get();
        source = buf.get();

        if (ALTE) {
            byte[] altBytes = new byte[4];
            buf.get(altBytes, 0, 3);
            altBytes[3] = 0x00;
            altitude = Integer.toUnsignedLong(ByteBuffer.wrap(altBytes).order(ByteOrder.LITTLE_ENDIAN).getInt());
        }
    }

    @Override
    public byte[] encode() throws Exception {
        ByteBuffer buf = ByteBuffer.allocate(32).order(ByteOrder.LITTLE_ENDIAN);

        long seconds = navigationTime.toEpochSecond(ZoneOffset.UTC) - START_DATE.toEpochSecond(ZoneOffset.UTC);
        buf.putInt((int) seconds);

        long latRaw = (long) (latitude / 90 * 0xFFFFFFFFL);
        long lonRaw = (long) (longitude / 180 * 0xFFFFFFFFL);
        buf.putInt((int) latRaw);
        buf.putInt((int) lonRaw);

        byte flags = 0;
        flags |= (ALTE ? 1 : 0) << 7;
        flags |= (LOHS ? 1 : 0) << 6;
        flags |= (LAHS ? 1 : 0) << 5;
        flags |= (MV   ? 1 : 0) << 4;
        flags |= (BB   ? 1 : 0) << 3;
        flags |= (CS   ? 1 : 0) << 2;
        flags |= (FIX  ? 1 : 0) << 1;
        flags |= (VLD  ? 1 : 0);
        buf.put(flags);

        int dirHiBit = (direction >> 7) & 1;
        int spdEnc = (speed * 10) & 0x3FFF;
        spdEnc |= (altitudeSign & 1) << 14;
        spdEnc |= (dirHiBit & 1) << 15;
        buf.putShort((short) spdEnc);

        buf.put((byte) (direction & 0x7F)); // только младшие 7 бит

        int odo = (int) odometer;
        buf.put((byte) (odo & 0xFF));
        buf.put((byte) ((odo >> 8) & 0xFF));
        buf.put((byte) ((odo >> 16) & 0xFF));

        buf.put(digitalInputs);
        buf.put(source);

        if (ALTE) {
            int alt = (int) altitude;
            buf.put((byte) (alt & 0xFF));
            buf.put((byte) ((alt >> 8) & 0xFF));
            buf.put((byte) ((alt >> 16) & 0xFF));
        }

        byte[] result = new byte[buf.position()];
        buf.flip();
        buf.get(result);
        return result;
    }

    @Override
    public short length() {
        try {
            return (short) encode().length;
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
                ", speed=" + speed +
                ", direction=" + direction +
                ", odometer=" + odometer +
                ", altitude=" + altitude +
                ", ALTE=" + ALTE +
                ", VLD=" + VLD +
                '}';
    }
}
