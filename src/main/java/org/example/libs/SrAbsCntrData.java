package org.example.libs;

import lombok.Data;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Data
public class SrAbsCntrData implements BinaryData {
    private byte counterNumber;
    private int counterValue;

    @Override
    public void decode(byte[] data) throws IOException {
        if (data.length < 4) {
            throw new IOException("Недостаточно данных для SrAbsCntrData");
        }

        ByteBuffer buf = ByteBuffer.wrap(data);
        buf.order(ByteOrder.LITTLE_ENDIAN);

        this.counterNumber = buf.get();

        int fullValue = 0;
        fullValue |= (buf.get() & 0xFF);       // байт 1
        fullValue |= (buf.get() & 0xFF) << 8;  // байт 2
        fullValue |= (buf.get() & 0xFF) << 16;
        this.counterValue = fullValue & 0x00FFFFFF;
    }

    @Override
    public byte[] encode() throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(4);
        buf.order(ByteOrder.LITTLE_ENDIAN);

        buf.put(counterNumber);
        buf.put((byte) counterValue);         // байт 2
        buf.put((byte) (counterValue >> 8));  // байт 3
        buf.put((byte) (counterValue >> 16));

        byte[] result = new byte[buf.position()];
        buf.flip();
        buf.get(result);
        return result;
    }

    @Override
    public int length() {
        try {
            return encode().length;
        } catch (IOException e) {
            return 0;
        }
    }
}
