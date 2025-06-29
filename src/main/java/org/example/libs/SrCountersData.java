package org.example.libs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

// SrCountersData структура подзаписи типа EGTS_SR_COUNTERS_DATA, которая используется аппаратно-программным
// комплексом для передачи на абонентский терминал данных о значении счетных входов
public class SrCountersData {
    public String CounterFieldExists1;
    public String CounterFieldExists2;
    public String CounterFieldExists3;
    public String CounterFieldExists4;
    public String CounterFieldExists5;
    public String CounterFieldExists6;
    public String CounterFieldExists7;
    public String CounterFieldExists8;

    public long Counter1;
    public long Counter2;
    public long Counter3;
    public long Counter4;
    public long Counter5;
    public long Counter6;
    public long Counter7;
    public long Counter8;

    // decode разбирает байты в структуру подзаписи
    public void decode(byte[] content) throws IOException {
        ByteArrayInputStream buf = new ByteArrayInputStream(content);

        int flags = buf.read();
        if (flags == -1) {
            throw new IOException("Не удалось получить байт цифровых выходов sr_counters_data");
        }
        String flagBits = String.format("%8s", Integer.toBinaryString(flags & 0xFF)).replace(' ', '0');

        CounterFieldExists8 = flagBits.substring(0, 1);
        CounterFieldExists7 = flagBits.substring(1, 2);
        CounterFieldExists6 = flagBits.substring(2, 3);
        CounterFieldExists5 = flagBits.substring(3, 4);
        CounterFieldExists4 = flagBits.substring(4, 5);
        CounterFieldExists3 = flagBits.substring(5, 6);
        CounterFieldExists2 = flagBits.substring(6, 7);
        CounterFieldExists1 = flagBits.substring(7);

        byte[] tmpBuf = new byte[3];
        byte[] counterVal;

        if (CounterFieldExists1.equals("1")) {
            if (buf.read(tmpBuf) != 3) throw new IOException("Не удалось получить показания CN1");
            counterVal = new byte[]{tmpBuf[0], tmpBuf[1], tmpBuf[2], 0};
            Counter1 = ByteBuffer.wrap(counterVal).order(ByteOrder.LITTLE_ENDIAN).getInt() & 0xFFFFFFFFL;
        }
        if (CounterFieldExists2.equals("1")) {
            if (buf.read(tmpBuf) != 3) throw new IOException("Не удалось получить показания CN2");
            counterVal = new byte[]{tmpBuf[0], tmpBuf[1], tmpBuf[2], 0};
            Counter2 = ByteBuffer.wrap(counterVal).order(ByteOrder.LITTLE_ENDIAN).getInt() & 0xFFFFFFFFL;
        }
        if (CounterFieldExists3.equals("1")) {
            if (buf.read(tmpBuf) != 3) throw new IOException("Не удалось получить показания CN3");
            counterVal = new byte[]{tmpBuf[0], tmpBuf[1], tmpBuf[2], 0};
            Counter3 = ByteBuffer.wrap(counterVal).order(ByteOrder.LITTLE_ENDIAN).getInt() & 0xFFFFFFFFL;
        }
        if (CounterFieldExists4.equals("1")) {
            if (buf.read(tmpBuf) != 3) throw new IOException("Не удалось получить показания CN4");
            counterVal = new byte[]{tmpBuf[0], tmpBuf[1], tmpBuf[2], 0};
            Counter4 = ByteBuffer.wrap(counterVal).order(ByteOrder.LITTLE_ENDIAN).getInt() & 0xFFFFFFFFL;
        }
        if (CounterFieldExists5.equals("1")) {
            if (buf.read(tmpBuf) != 3) throw new IOException("Не удалось получить показания CN5");
            counterVal = new byte[]{tmpBuf[0], tmpBuf[1], tmpBuf[2], 0};
            Counter5 = ByteBuffer.wrap(counterVal).order(ByteOrder.LITTLE_ENDIAN).getInt() & 0xFFFFFFFFL;
        }
        if (CounterFieldExists6.equals("1")) {
            if (buf.read(tmpBuf) != 3) throw new IOException("Не удалось получить показания CN6");
            counterVal = new byte[]{tmpBuf[0], tmpBuf[1], tmpBuf[2], 0};
            Counter6 = ByteBuffer.wrap(counterVal).order(ByteOrder.LITTLE_ENDIAN).getInt() & 0xFFFFFFFFL;
        }
        if (CounterFieldExists7.equals("1")) {
            if (buf.read(tmpBuf) != 3) throw new IOException("Не удалось получить показания CN7");
            counterVal = new byte[]{tmpBuf[0], tmpBuf[1], tmpBuf[2], 0};
            Counter7 = ByteBuffer.wrap(counterVal).order(ByteOrder.LITTLE_ENDIAN).getInt() & 0xFFFFFFFFL;
        }
        if (CounterFieldExists8.equals("1")) {
            if (buf.read(tmpBuf) != 3) throw new IOException("Не удалось получить показания CN8");
            counterVal = new byte[]{tmpBuf[0], tmpBuf[1], tmpBuf[2], 0};
            Counter8 = ByteBuffer.wrap(counterVal).order(ByteOrder.LITTLE_ENDIAN).getInt() & 0xFFFFFFFFL;
        }
    }

    // encode преобразовывает подзапись в набор байт
    public byte[] encode() throws IOException {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        String flagsBits = CounterFieldExists8 + CounterFieldExists7 + CounterFieldExists6 + CounterFieldExists5 +
                CounterFieldExists4 + CounterFieldExists3 + CounterFieldExists2 + CounterFieldExists1;
        int flags = Integer.parseInt(flagsBits, 2);
        buf.write(flags);

        ByteBuffer sensVal = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);

        if (CounterFieldExists1.equals("1")) {
            sensVal.putInt(0, (int) Counter1);
            buf.write(sensVal.array(), 0, 3);
        }
        if (CounterFieldExists2.equals("1")) {
            sensVal.putInt(0, (int) Counter2);
            buf.write(sensVal.array(), 0, 3);
        }
        if (CounterFieldExists3.equals("1")) {
            sensVal.putInt(0, (int) Counter3);
            buf.write(sensVal.array(), 0, 3);
        }
        if (CounterFieldExists4.equals("1")) {
            sensVal.putInt(0, (int) Counter4);
            buf.write(sensVal.array(), 0, 3);
        }
        if (CounterFieldExists5.equals("1")) {
            sensVal.putInt(0, (int) Counter5);
            buf.write(sensVal.array(), 0, 3);
        }
        if (CounterFieldExists6.equals("1")) {
            sensVal.putInt(0, (int) Counter6);
            buf.write(sensVal.array(), 0, 3);
        }
        if (CounterFieldExists7.equals("1")) {
            sensVal.putInt(0, (int) Counter7);
            buf.write(sensVal.array(), 0, 3);
        }
        if (CounterFieldExists8.equals("1")) {
            sensVal.putInt(0, (int) Counter8);
            buf.write(sensVal.array(), 0, 3);
        }

        return buf.toByteArray();
    }

    public int length() {
        try {
            return encode().length;
        } catch (IOException e) {
            return 0;
        }
    }
}
