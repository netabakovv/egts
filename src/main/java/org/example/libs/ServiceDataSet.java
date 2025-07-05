package org.example.libs;

import lombok.Data;
import lombok.Getter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

@Data
public class ServiceDataSet implements BinaryData {

    private final List<ServiceDataRecord> records = new ArrayList<>();

    public void addRecord(ServiceDataRecord record) {
        records.add(record);
    }

    @Override
    public void decode(byte[] data) throws IOException {
        int offset = 0;
        ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);

        while (buffer.remaining() >= 7) { // Минимум для заголовка записи
            int startPos = buffer.position();

            // Считаем recordLength (2 байта)
            if (buffer.remaining() < 2) {
                throw new IOException("Недостаточно данных для чтения recordLength");
            }
            int recordLength = buffer.getShort() & 0xFFFF;

            // Считаем recordNumber (2 байта)
            if (buffer.remaining() < 2) {
                throw new IOException("Недостаточно данных для чтения recordNumber");
            }
            int recordNumber = buffer.getShort() & 0xFFFF;

            // Считаем flags (1 байт)
            if (buffer.remaining() < 1) {
                throw new IOException("Недостаточно данных для чтения flags");
            }
            byte flags = buffer.get();
            String flagBits = String.format("%8s", Integer.toBinaryString(flags & 0xFF)).replace(' ', '0');

            // Подсчитываем размер дополнительных полей
            int extraFieldsSize = 0;
            if (flagBits.charAt(7) == '1') extraFieldsSize += 4; // ObjectID
            if (flagBits.charAt(6) == '1') extraFieldsSize += 4; // EventID
            if (flagBits.charAt(5) == '1') extraFieldsSize += 4; // Time

            // Должно быть минимум 2 байта на SourceServiceType + RecipientServiceType
            int fixedTailSize = 2;

            // Общая длина записи (заголовки + payload)
            int totalRecordSize = 2 /* recordLength */ + 2 /* recordNumber */ + 1 /* flags */
                    + extraFieldsSize + fixedTailSize + recordLength;

            // Проверяем, что осталось достаточно байт
            if (buffer.remaining() < (totalRecordSize - (buffer.position() - startPos))) {
                throw new IOException("Недостаточно данных для полной записи ServiceDataRecord");
            }

            // Вернёмся к началу записи, чтобы вырезать весь блок целиком
            buffer.position(startPos);
            byte[] chunk = new byte[totalRecordSize];
            buffer.get(chunk);

            // Декодируем подзапись
            ServiceDataRecord record = new ServiceDataRecord();
            record.decode(chunk);
            records.add(record);
        }

        if (buffer.remaining() > 0) {
            throw new IOException("Остаток данных не распарсился как ServiceDataRecord");
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
