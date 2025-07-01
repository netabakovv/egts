package org.example.libs;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.nio.ByteBuffer;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SrDispatcherIdentity implements BinaryData{
    private byte dispatcherType; // DT (Dispatcher Type)
    private int dispatcherID;    // DID (Dispatcher ID)
    private String description;  // DSCR (Description)

    /**
     * Декодирует байты в структуру SrDispatcherIdentity.
     */
    @Override
    public void decode(byte[] content) throws IOException{
        if (content == null || content.length < 5) {
            throw new IllegalArgumentException("Недостаточно данных для декодирования SrDispatcherIdentity");
        }

        ByteBuffer buf = ByteBuffer.wrap(content);

        // Читаем тип диспетчера (1 байт)
        this.dispatcherType = buf.get();

        // Читаем DispatcherID (4 байта, Little Endian)
        if (buf.remaining() < 4) {
            throw new IllegalArgumentException("Недостаточно данных для чтения DispatcherID");
        }
        this.dispatcherID = Integer.reverseBytes(buf.getInt());

        // Оставшиеся байты — строка Description
        int remaining = buf.remaining();
        if (remaining > 0) {
            byte[] descBytes = new byte[remaining];
            buf.get(descBytes);
            this.description = new String(descBytes);
        } else {
            this.description = "";
        }
    }

    /**
     * Кодирует структуру в массив байт.
     */
    @Override
    public byte[] encode() throws IOException {
        byte[] descBytes = description != null ? description.getBytes() : new byte[0];

        ByteBuffer buf = ByteBuffer.allocate(1 + 4 + descBytes.length);

        // Пишем тип диспетчера (1 байт)
        buf.put(dispatcherType);

        // Пишем DispatcherID (4 байта, Little Endian)
        buf.order(java.nio.ByteOrder.LITTLE_ENDIAN);
        buf.putInt(dispatcherID);

        // Пишем описание
        buf.put(descBytes);

        return buf.array();
    }

    /**
     * Возвращает длину закодированной подзаписи.
     */
    @Override
    public int length() {
        try {
            return encode().length;
        } catch (Exception e) {
            return 0;
        }
    }
}
