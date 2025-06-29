package org.example.libs;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Getter
@Setter
public class SrResponse implements BinaryData{
    private int confirmedRecordNumber; // uint16
    private short recordStatus; // uint8

    @Override
    public void decode(byte[] data) throws Exception {
        ByteBuffer buf = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);

        if (data.length < 3) {
            throw new IOException("Недостаточно данных для разбора SrResponse");
        }

        confirmedRecordNumber = Short.toUnsignedInt(buf.getShort());
        recordStatus = (short) Byte.toUnsignedInt(buf.get());

        // TODO: возможно пихнуть ServiceDataSet
        // Пропускаем декодирование ServiceDataSet, т.к. его нет в структуре SrResponse в Java
        // В оригинале это Go-ошибка (некорректный вызов sfd.Decode на оставшихся байтах)
    }

    @Override
    public byte[] encode() throws Exception {
        ByteBuffer buf = ByteBuffer.allocate(3).order(ByteOrder.LITTLE_ENDIAN);

        buf.putShort((short) confirmedRecordNumber);
        buf.put((byte) recordStatus);

        return buf.array();
    }

    @Override
    public short length() {
        return 3;
    }
}
