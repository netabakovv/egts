package org.example.libs;

import lombok.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDataRecord implements BinaryData {

    private static final long TIME_OFFSET_SECONDS = LocalDateTime.of(2010, 1, 1, 0, 0).toEpochSecond(ZoneOffset.UTC);

    private int recordLength;
    private int recordNumber;
    private String sourceServiceOnDevice = "0";
    private String recipientServiceOnDevice = "0";
    private String group = "0";
    private String recordProcessingPriority = "00";
    private String timeFieldExists = "0";
    private String eventIDFieldExists = "0";
    private String objectIDFieldExists = "0";

    private int objectIdentifier;
    private int eventIdentifier;
    private Instant time;

    private byte sourceServiceType;
    private byte recipientServiceType;

    private RecordDataSet recordDataSet;

    @Override
    public void decode(byte[] data) throws IOException {
        ByteBuffer buf = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);

        // 1) Читаем recordLength и сразу избавляемся от знаковых значений
        int recordLength = Short.toUnsignedInt(buf.getShort());

// Читаем recordNumber, flags и optional поля
        this.recordNumber = Short.toUnsignedInt(buf.getShort());
        // TODO Тут хуйня какя-то, кирилл исправляй
        byte flags = buf.get();
// … если биты flags говорят, что есть ObjectID, EventID, Time — читайте buf.getInt() соответствующее количество раз
        byte sourceService = buf.get();
        byte recipientService = buf.get();

// Правильный подсчёт заголовочных байт:
        int headerBytes = buf.position() - 2;  // 2 байта for recordLength
        int rdsLength = recordLength - headerBytes;

        System.out.printf(">>> Debug ServiceDataRecord: recordLength=%d, headerBytes=%d, rdsLength=%d, buf.remaining() before read=%d%n", recordLength, headerBytes, rdsLength, buf.remaining());

// Проверяем
        if (rdsLength < 0 || buf.remaining() < rdsLength) {
            throw new IOException(String.format("ServiceDataRecord: declared %d, headerBytes=%d ⇒ rdsLength=%d but remaining=%d", recordLength, headerBytes, rdsLength, buf.remaining()));
        }

// Вырезаем и decode
        byte[] rdsBytes = new byte[rdsLength];
        buf.get(rdsBytes);
        this.recordDataSet = new RecordDataSet();
        recordDataSet.decode(rdsBytes);
    }


    @Override
    public byte[] encode() throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); DataOutputStream dos = new DataOutputStream(baos)) {

            byte[] rdsBytes = recordDataSet != null ? recordDataSet.encode() : new byte[0];

            if (recordLength == 0) {
                recordLength = (short) rdsBytes.length;
            }

            writeShortLE(dos, recordLength);
            writeShortLE(dos, recordNumber);

            String flagBits = sourceServiceOnDevice + recipientServiceOnDevice + group + recordProcessingPriority + timeFieldExists + eventIDFieldExists + objectIDFieldExists;

            byte flags = (byte) Integer.parseInt(flagBits, 2);
            dos.writeByte(flags);

            if (objectIDFieldExists.equals("1")) {
                writeIntLE(dos, objectIdentifier);
            }

            if (eventIDFieldExists.equals("1")) {
                writeIntLE(dos, eventIdentifier);
            }

            if (timeFieldExists.equals("1")) {
                long seconds = time.getEpochSecond() - TIME_OFFSET_SECONDS;
                writeIntLE(dos, (int) seconds);
            }

            dos.writeByte(sourceServiceType);
            dos.writeByte(recipientServiceType);

            dos.write(rdsBytes);
            return baos.toByteArray();
        }
    }

    private void writeShortLE(DataOutputStream dos, int value) throws IOException {
        dos.writeByte(value & 0xFF);
        dos.writeByte((value >> 8) & 0xFF);
    }

    private void writeIntLE(DataOutputStream dos, int value) throws IOException {
        dos.writeByte(value & 0xFF);
        dos.writeByte((value >> 8) & 0xFF);
        dos.writeByte((value >> 16) & 0xFF);
        dos.writeByte((value >> 24) & 0xFF);
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
