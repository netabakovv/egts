package org.example.libs;

import lombok.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Getter
@Setter
public class SrAbsSensData implements BinaryData{
    private byte sensorNumber;
    private int value;

    @Override
    public void decode(byte[] data) throws IOException {
        if (data.length < length()) {
            throw new IOException("Некорректный размер данных для SrAdbSensData");
        }

        ByteBuffer buf = ByteBuffer.wrap(data);
        buf.order(ByteOrder.LITTLE_ENDIAN);

        this.sensorNumber = buf.get();

        int fullValue = buf.getInt(0);
        this.value = fullValue >>> 8;
    }

    @Override
    public byte[] encode() throws IOException {
        return new byte[]{
                sensorNumber,
                (byte) (value),
                (byte) (value >>> 8),
                (byte) (value >>> 16)
        };
    }

    @Override
    public int length() {
        return 4;
    }
}
