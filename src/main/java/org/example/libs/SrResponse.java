package org.example.libs;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Data
public class SrResponse implements BinaryData{
    private int confirmedRecordNumber; // uint16
    private short recordStatus; // uint8

    @Override
    public void decode(byte[] data) throws IOException {
        ByteBuffer buf = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);

        if (data.length < 3) {
            throw new IOException("Недостаточно данных для разбора SrResponse");
        }

        confirmedRecordNumber = Short.toUnsignedInt(buf.getShort());
        recordStatus = (short) Byte.toUnsignedInt(buf.get());

        if (buf.remaining() > 0) {
            byte[] remaining = new byte[buf.remaining()];
            buf.get(remaining);

            ServiceDataSet dataSet = new ServiceDataSet();
            dataSet.decode(remaining);
            // TODO: сохранить, если надо — например, this.serviceDataSet = dataSet;
        }
    }

    @Override
    public byte[] encode() throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(3).order(ByteOrder.LITTLE_ENDIAN);

        buf.putShort((short) confirmedRecordNumber);
        buf.put((byte) recordStatus);

        return buf.array();
    }

    @Override
    public int length() {
        return 3;
    }
}
