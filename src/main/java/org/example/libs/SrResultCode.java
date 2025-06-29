package org.example.libs;

import lombok.Getter;
import lombok.Setter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Getter
@Setter
public class SrResultCode implements BinaryData{
    private byte resultCode;

    @Override
    public void decode(byte[] data) throws Exception {
        if (data == null || data.length < 1) {
            throw new IOException("Данные пустые или недостаточная длина для SrResultCode");
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        int read = bais.read();
        if (read == -1) {
            throw new IOException("Не удалось получить код результата");
        }

        resultCode = (byte) read;
    }

    @Override
    public byte[] encode() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(resultCode);
        return baos.toByteArray();
    }

    @Override
    public short length() {
        return 1; // всегда 1 байт
    }

    @Override
    public String toString() {
        return "SrResultCode{resultCode=" + (resultCode & 0xFF) + '}';
    }
}
