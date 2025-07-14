package org.example.libs;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class SrCountersData implements BinaryData {
    private String counterFieldExists1 = "0";
    private String counterFieldExists2 = "0";
    private String counterFieldExists3 = "0";
    private String counterFieldExists4 = "0";
    private String counterFieldExists5 = "0";
    private String counterFieldExists6 = "0";
    private String counterFieldExists7 = "0";
    private String counterFieldExists8 = "0";

    private long counter1;
    private long counter2;
    private long counter3;
    private long counter4;
    private long counter5;
    private long counter6;
    private long counter7;
    private long counter8;

    /**
     * Декодирует байты в структуру SrCountersData.
     */
    @Override
    public void decode(byte[] content) throws IOException {
        if (content == null || content.length < 1) {
            throw new IllegalArgumentException("Недостаточно данных для декодирования.");
        }

        ByteBuffer buffer = ByteBuffer.wrap(content).order(ByteOrder.LITTLE_ENDIAN);

        byte flags = buffer.get();

        // Читаем флаги: старший бит первым (C1...C8)
        this.counterFieldExists1 = ((flags >> 7) & 0x01) == 1 ? "1" : "0";
        this.counterFieldExists2 = ((flags >> 6) & 0x01) == 1 ? "1" : "0";
        this.counterFieldExists3 = ((flags >> 5) & 0x01) == 1 ? "1" : "0";
        this.counterFieldExists4 = ((flags >> 4) & 0x01) == 1 ? "1" : "0";
        this.counterFieldExists5 = ((flags >> 3) & 0x01) == 1 ? "1" : "0";
        this.counterFieldExists6 = ((flags >> 2) & 0x01) == 1 ? "1" : "0";
        this.counterFieldExists7 = ((flags >> 1) & 0x01) == 1 ? "1" : "0";
        this.counterFieldExists8 = (flags & 0x01) == 1 ? "1" : "0";

        // Теперь считываем счетчики
        if ("1".equals(counterFieldExists1) && buffer.remaining() >= 3) {
            this.counter1 = readCounter(buffer);
        }

        if ("1".equals(counterFieldExists2) && buffer.remaining() >= 3) {
            this.counter2 = readCounter(buffer);
        }

        if ("1".equals(counterFieldExists3) && buffer.remaining() >= 3) {
            this.counter3 = readCounter(buffer);
        }

        if ("1".equals(counterFieldExists4) && buffer.remaining() >= 3) {
            this.counter4 = readCounter(buffer);
        }

        if ("1".equals(counterFieldExists5) && buffer.remaining() >= 3) {
            this.counter5 = readCounter(buffer);
        }

        if ("1".equals(counterFieldExists6) && buffer.remaining() >= 3) {
            this.counter6 = readCounter(buffer);
        }

        if ("1".equals(counterFieldExists7) && buffer.remaining() >= 3) {
            this.counter7 = readCounter(buffer);
        }

        if ("1".equals(counterFieldExists8) && buffer.remaining() >= 3) {
            this.counter8 = readCounter(buffer);
        }
    }

    private long readCounter(ByteBuffer buffer) throws IOException {
        if (buffer.remaining() < 3) {
            throw new IOException("Недостаточно данных для чтения 3-байтного счетчика");
        }

        byte[] bytes = new byte[3];
        buffer.get(bytes);

        byte[] full = new byte[4];
        System.arraycopy(bytes, 0, full, 0, 3);
        return (ByteBuffer.wrap(full).order(ByteOrder.LITTLE_ENDIAN).getInt() & 0xFFFFFFFFL);
    }

    /**
     * Кодирует структуру в массив байт.
     */
    @Override
    public byte[] encode() throws IOException{
        StringBuilder flagBuilder = new StringBuilder();
        flagBuilder.append(counterFieldExists8)
                .append(counterFieldExists7)
                .append(counterFieldExists6)
                .append(counterFieldExists5)
                .append(counterFieldExists4)
                .append(counterFieldExists3)
                .append(counterFieldExists2)
                .append(counterFieldExists1);

        String flagStr = flagBuilder.toString();

        if (flagStr.length() != 8) {
            throw new IllegalArgumentException("Флаг должен быть длиной 8 символов");
        }

        for (int i = 0; i < flagStr.length(); i++) {
            char c = flagStr.charAt(i);
            if (c != '0' && c != '1') {
                throw new IllegalArgumentException("Флаг содержит недопустимый символ на позиции " + i + ": '" + c + "'");
            }
        }

        int flags;
        try {
            flags = Integer.parseInt(flagStr, 2);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Не удалось преобразовать флаг в число", e);
        }

        ByteBuffer buffer = ByteBuffer.allocate(1 + 3 * countEnabledFlags(flagStr));

        buffer.put((byte) flags);

        if ("1".equals(counterFieldExists1)) {
            buffer.put(encodeCounter(counter1));
        }

        if ("1".equals(counterFieldExists2)) {
            buffer.put(encodeCounter(counter2));
        }

        if ("1".equals(counterFieldExists3)) {
            buffer.put(encodeCounter(counter3));
        }

        if ("1".equals(counterFieldExists4)) {
            buffer.put(encodeCounter(counter4));
        }

        if ("1".equals(counterFieldExists5)) {
            buffer.put(encodeCounter(counter5));
        }

        if ("1".equals(counterFieldExists6)) {
            buffer.put(encodeCounter(counter6));
        }

        if ("1".equals(counterFieldExists7)) {
            buffer.put(encodeCounter(counter7));
        }

        if ("1".equals(counterFieldExists8)) {
            buffer.put(encodeCounter(counter8));
        }

        byte[] result = new byte[buffer.position()];
        buffer.rewind();
        buffer.get(result);

        return result;
    }

    private int countEnabledFlags(String flagStr) {
        int count = 0;
        for (int i = 0; i < flagStr.length(); i++) {
            if (flagStr.charAt(i) == '1') count++;
        }
        return count;
    }

    private byte[] encodeCounter(long value) {
        if (value < 0 || value > 0xFFFFFFFFL) {
            throw new IllegalArgumentException("Значение вне диапазона uint32: " + value);
        }

        byte[] full = ByteBuffer.allocate(4)
                .order(java.nio.ByteOrder.LITTLE_ENDIAN)
                .putInt((int) value)
                .array();

        byte[] threeBytes = new byte[3];
        System.arraycopy(full, 0, threeBytes, 0, 3);
        return threeBytes;
    }

    public List<CounterEntry> getAllCounters() {
        List<CounterEntry> result = new ArrayList<>();

        if ("1".equals(counterFieldExists1)) result.add(new CounterEntry(1, counter1));
        if ("1".equals(counterFieldExists2)) result.add(new CounterEntry(2, counter2));
        if ("1".equals(counterFieldExists3)) result.add(new CounterEntry(3, counter3));
        if ("1".equals(counterFieldExists4)) result.add(new CounterEntry(4, counter4));
        if ("1".equals(counterFieldExists5)) result.add(new CounterEntry(5, counter5));
        if ("1".equals(counterFieldExists6)) result.add(new CounterEntry(6, counter6));
        if ("1".equals(counterFieldExists7)) result.add(new CounterEntry(7, counter7));
        if ("1".equals(counterFieldExists8)) result.add(new CounterEntry(8, counter8));

        return result;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CounterEntry {
        private int counterNumber;
        private long counterValue;
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