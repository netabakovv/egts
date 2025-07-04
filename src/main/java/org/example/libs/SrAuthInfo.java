package org.example.libs;

import lombok.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Getter
@Setter
public class SrAuthInfo implements BinaryData{
    private static final byte SEPARATOR = 0x00;

    private String userName;
    private String userPassword;
    private String serverSequence;

    @Override
    public void decode(byte[] data) throws IOException {
        ByteBuffer buf = ByteBuffer.wrap(data);
        buf.mark();

        try {
            this.userName = readNullTerminatedString(buf);
            this.userPassword = readNullTerminatedString(buf);

            if (buf.hasRemaining()) {
                this.serverSequence = readNullTerminatedString(buf);
            } else {
                this.serverSequence = null;
            }
        } catch (IOException e) {
            throw new IOException("Ошибка декодирования SrAuthInfo: " + e.getMessage());
        }
    }

    @Override
    public byte[] encode() throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(1024);

        // userName + 0x00
        putStringWithSeparator(buf, userName, true);

        // userPassword + 0x00
        putStringWithSeparator(buf, userPassword, true);

        // serverSequence — если есть, то тоже + 0x00
        if (serverSequence != null && !serverSequence.isEmpty()) {
            putStringWithSeparator(buf, serverSequence, false); // Нет завершающего 0x00
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

    private void putStringWithSeparator(ByteBuffer buf, String str, boolean addTrailingSeparator) {
        if (str != null && !str.isEmpty()) {
            byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
            buf.put(bytes);
            if (addTrailingSeparator) {
                buf.put(SEPARATOR);
            }
        } else {
            buf.put(SEPARATOR);
        }
    }

    private String readNullTerminatedString(ByteBuffer buf) throws IOException {
        StringBuilder sb = new StringBuilder();
        boolean found = false;

        while (buf.hasRemaining()) {
            byte b = buf.get();
            if (b == SEPARATOR) {
                found = true;
                break;
            }
            sb.append((char) b);
        }

        if (!found && sb.isEmpty()) {
            throw new IOException("Неверный формат строки: отсутствует разделитель или данные");
        }

        return sb.toString();
    }
}
