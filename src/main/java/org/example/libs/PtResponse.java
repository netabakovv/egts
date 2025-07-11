package org.example.libs;

import lombok.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Data
public class PtResponse implements BinaryData {
    private int responsePacketID;
    private byte processingResult;
    private BinaryData sdr;

    @Override
    public void decode(byte[] data) throws IOException {
        ByteBuffer buf = ByteBuffer.wrap(data);
        buf.order(ByteOrder.LITTLE_ENDIAN);

        if (buf.remaining() < 2) {
            throw new IOException("Недостаточно данных для чтения ResponsePacketID");
        }
        this.responsePacketID = buf.getShort();

        if (buf.hasRemaining()) {
            this.processingResult = buf.get();
        }

        if (buf.hasRemaining()) {
            byte[] sdrBytes = new byte[buf.remaining()];
            buf.get(sdrBytes);

            this.sdr = new ServiceDataSet();
            this.sdr.decode(sdrBytes);
        }
    }

    @Override
    public byte[] encode() throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(1024);
        buf.order(ByteOrder.LITTLE_ENDIAN);

        buf.putShort((short) responsePacketID);
        buf.put(processingResult);

        if (sdr != null) {
            byte[] sdrBytes = sdr.encode();
            buf.put(sdrBytes);
        }

        byte[] result = new byte[buf.position()];
        buf.rewind();
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
