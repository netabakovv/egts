package org.example.libs;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

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

        int fullValue = buf.getInt();
        this.counterValue = fullValue & 0x00FFFFFF;
    }

    @Override
    public byte[] encode() throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(4);
        buf.order(ByteOrder.LITTLE_ENDIAN);

        buf.put(counterNumber);

        buf.putInt(counterValue);
        byte[] allBytes = buf.array();
        byte[] result = new  byte[4];
        System.arraycopy(allBytes, 0, result, 0, 4);
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
