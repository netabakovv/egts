package org.example.libs;

import lombok.Getter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ServiceDataSet implements BinaryData {

    private final List<ServiceDataRecord> records = new ArrayList<>();

    public void addRecord(ServiceDataRecord record) {
        records.add(record);
    }

    @Override
    public void decode(byte[] data) throws IOException {
        int offset = 0;

        while (offset < data.length) {
            // Минимальный размер SDR-записи: 2 (length) + 2 (number) + 1 (flags) + 2 (types) = 7 байт
            if (data.length - offset < 7) {
                throw new IOException("Недостаточно данных для SDR");
            }

            // Получаем длину полезной части (RecordLength)
            int recordLength = ((data[offset + 1] & 0xFF) << 8) | (data[offset] & 0xFF);

            // Ищем конец записи SDR, прибавляя всё остальное, включая заголовки
            int currentOffset = offset + 2; // сместились после recordLength
            currentOffset += 2; // recordNumber
            currentOffset += 1; // flags

            byte flags = data[offset + 4];
            String flagBits = String.format("%8s", Integer.toBinaryString(flags & 0xFF)).replace(' ', '0');

            if (flagBits.charAt(7) == '1') currentOffset += 4; // ObjectID
            if (flagBits.charAt(6) == '1') currentOffset += 4; // EventID
            if (flagBits.charAt(5) == '1') currentOffset += 4; // Time

            currentOffset += 1; // SourceServiceType
            currentOffset += 1; // RecipientServiceType
            currentOffset += recordLength; // Payload

            int chunkLen = Math.min(currentOffset - offset, data.length - offset);
            byte[] chunk = new byte[chunkLen];
            System.arraycopy(data, offset, chunk, 0, chunkLen);

            ServiceDataRecord record = new ServiceDataRecord();
            record.decode(chunk);
            records.add(record);

            offset += chunkLen;
        }
    }


    @Override
    public byte[] encode() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (ServiceDataRecord record : records) {
            baos.write(record.encode());
        }
        return baos.toByteArray();
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
