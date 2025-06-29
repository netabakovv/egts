package org.example.libs;

import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Getter
@Setter
public class ServiceDataRecord implements BinaryData {

    private static final long TIME_OFFSET_SECONDS = LocalDateTime.of(2010, 1, 1, 0, 0).toEpochSecond(ZoneOffset.UTC);

    private short recordLength;
    private short recordNumber;
    private String sourceServiceOnDevice;
    private String recipientServiceOnDevice;
    private String group;
    private String recordProcessingPriority;
    private String timeFieldExists;
    private String eventIDFieldExists;
    private String objectIDFieldExists;

    private int objectIdentifier;
    private int eventIdentifier;
    private Instant time;

    private byte sourceServiceType;
    private byte recipientServiceType;

    private RecordDataSet recordDataSet;

    @Override
    public void decode(byte[] data) throws IOException {
        ByteBuffer buf = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);

        recordLength = buf.getShort();
        recordNumber = buf.getShort();

        byte flags = buf.get();
        String flagBits = String.format("%8s", Integer.toBinaryString(flags & 0xFF)).replace(' ', '0');

        sourceServiceOnDevice = flagBits.substring(0, 1);
        recipientServiceOnDevice = flagBits.substring(1, 2);
        group = flagBits.substring(2, 3);
        recordProcessingPriority = flagBits.substring(3, 5);
        timeFieldExists = flagBits.substring(5, 6);
        eventIDFieldExists = flagBits.substring(6, 7);
        objectIDFieldExists = flagBits.substring(7, 8);

        if (objectIDFieldExists.equals("1")) {
            objectIdentifier = buf.getInt();
        }

        if (eventIDFieldExists.equals("1")) {
            eventIdentifier = buf.getInt();
        }

        if (timeFieldExists.equals("1")) {
            int seconds = buf.getInt();
            time = Instant.ofEpochSecond(TIME_OFFSET_SECONDS + seconds);
        }

        sourceServiceType = buf.get();
        recipientServiceType = buf.get();

        int remaining = buf.remaining();
        if (remaining > 0) {
            byte[] rdsBytes = new byte[remaining];
            buf.get(rdsBytes);

            recordDataSet = new RecordDataSet();
            recordDataSet.decode(rdsBytes);
        }
    }

    @Override
    public byte[] encode() throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(baos)) {

            byte[] rdsBytes = recordDataSet != null ? recordDataSet.encode() : new byte[0];

            if (recordLength == 0) {
                recordLength = (short) rdsBytes.length;
            }

            dos.writeShort(Short.toUnsignedInt(recordLength));
            dos.writeShort(Short.toUnsignedInt(recordNumber));

            String flagBits = sourceServiceOnDevice + recipientServiceOnDevice + group + recordProcessingPriority +
                    timeFieldExists + eventIDFieldExists + objectIDFieldExists;

            byte flags = (byte) Integer.parseInt(flagBits, 2);
            dos.writeByte(flags);

            if (objectIDFieldExists.equals("1")) {
                dos.writeInt(objectIdentifier);
            }

            if (eventIDFieldExists.equals("1")) {
                dos.writeInt(eventIdentifier);
            }

            if (timeFieldExists.equals("1")) {
                long seconds = time.getEpochSecond() - TIME_OFFSET_SECONDS;
                dos.writeInt((int) seconds);
            }

            dos.writeByte(sourceServiceType);
            dos.writeByte(recipientServiceType);

            dos.write(rdsBytes);
            return baos.toByteArray();
        }
    }

    @Override
    public int length() {
        try {
            return (short) encode().length;
        } catch (Exception e) {
            return 0;
        }
    }
}
