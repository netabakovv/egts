package org.example.libs;

import lombok.Getter;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ServiceDataSet implements BinaryData {

    private final List<ServiceDataRecord> records = new ArrayList<>();

    public void addRecord(ServiceDataRecord record) {
        records.add(record);
    }

    @Override
    public void decode(byte[] data) throws Exception {
        int offset = 0;

        while (offset < data.length) {
            // Сначала получим длину записи (2 байта, прямой порядок байтов)
            int recordLen = ((data[offset + 1] & 0xFF) << 8) | (data[offset] & 0xFF);
            int totalLen = recordLen + 12; // заголовок + данные (приблизительно)

            int chunkLen = Math.min(data.length - offset, totalLen);

            byte[] chunk = new byte[chunkLen];
            System.arraycopy(data, offset, chunk, 0, chunkLen);

            ServiceDataRecord record = new ServiceDataRecord();
            record.decode(chunk);
            records.add(record);

            offset += chunk.length;
        }
    }

    @Override
    public byte[] encode() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (ServiceDataRecord record : records) {
            baos.write(record.encode());
        }
        return baos.toByteArray();
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
