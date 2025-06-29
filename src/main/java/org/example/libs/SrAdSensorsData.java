package org.example.libs;

import lombok.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Getter
@Setter
public class SrAdSensorsData implements BinaryData {
    private String digitalInputsOctetExists1 = "0";
    private String digitalInputsOctetExists2 = "0";
    private String digitalInputsOctetExists3 = "0";
    private String digitalInputsOctetExists4 = "0";
    private String digitalInputsOctetExists5 = "0";
    private String digitalInputsOctetExists6 = "0";
    private String digitalInputsOctetExists7 = "0";
    private String digitalInputsOctetExists8 = "0";

    private byte digitalOutputs;

    private String analogSensorFieldExists1 = "0";
    private String analogSensorFieldExists2 = "0";
    private String analogSensorFieldExists3 = "0";
    private String analogSensorFieldExists4 = "0";
    private String analogSensorFieldExists5 = "0";
    private String analogSensorFieldExists6 = "0";
    private String analogSensorFieldExists7 = "0";
    private String analogSensorFieldExists8 = "0";

    private byte additionalDigitalInputsOctet1;
    private byte additionalDigitalInputsOctet2;
    private byte additionalDigitalInputsOctet3;
    private byte additionalDigitalInputsOctet4;
    private byte additionalDigitalInputsOctet5;
    private byte additionalDigitalInputsOctet6;
    private byte additionalDigitalInputsOctet7;
    private byte additionalDigitalInputsOctet8;

    private int analogSensor1;
    private int analogSensor2;
    private int analogSensor3;
    private int analogSensor4;
    private int analogSensor5;
    private int analogSensor6;
    private int analogSensor7;
    private int analogSensor8;

    @Override
    public void decode(byte[] data) throws IOException {
        ByteBuffer buf = ByteBuffer.wrap(data);
        buf.order(ByteOrder.LITTLE_ENDIAN);

        if (!buf.hasRemaining()) throw new IOException("Недостаточно данных для чтения флагов");

        byte flags = buf.get();

        int flagBits = Byte.toUnsignedInt(flags);
        this.digitalInputsOctetExists8 = ((flagBits >> 7) & 0x01) == 1 ? "1" : "0";
        this.digitalInputsOctetExists7 = ((flagBits >> 6) & 0x01) == 1 ? "1" : "0";
        this.digitalInputsOctetExists6 = ((flagBits >> 5) & 0x01) == 1 ? "1" : "0";
        this.digitalInputsOctetExists5 = ((flagBits >> 4) & 0x01) == 1 ? "1" : "0";
        this.digitalInputsOctetExists4 = ((flagBits >> 3) & 0x01) == 1 ? "1" : "0";
        this.digitalInputsOctetExists3 = ((flagBits >> 2) & 0x01) == 1 ? "1" : "0";
        this.digitalInputsOctetExists2 = ((flagBits >> 1) & 0x01) == 1 ? "1" : "0";
        this.digitalInputsOctetExists1 = (flagBits & 0x01) == 1 ? "1" : "0";

        if (!buf.hasRemaining()) throw new IOException("Недостаточно данных для чтений дискретных флагов");
        this.digitalOutputs = buf.get();

        if (!buf.hasRemaining()) throw new IOException("Недостаточно даннях для чтения флагов аналоговых датчиков");
        flags = buf.get();
        flagBits = Byte.toUnsignedInt(flags);

        this.analogSensorFieldExists1 = ((flagBits >> 7) & 0x01) == 1 ? "1" : "0";
        this.analogSensorFieldExists2 = ((flagBits >> 6) & 0x01) == 1 ? "1" : "0";
        this.analogSensorFieldExists3 = ((flagBits >> 5) & 0x01) == 1 ? "1" : "0";
        this.analogSensorFieldExists4 = ((flagBits >> 4) & 0x01) == 1 ? "1" : "0";
        this.analogSensorFieldExists5 = ((flagBits >> 3) & 0x01) == 1 ? "1" : "0";
        this.analogSensorFieldExists6 = ((flagBits >> 2) & 0x01) == 1 ? "1" : "0";
        this.analogSensorFieldExists7 = ((flagBits >> 1) & 0x01) == 1 ? "1" : "0";
        this.analogSensorFieldExists8 = (flagBits & 0x01) == 1 ? "1" : "0";

        if ("1".equals(digitalInputsOctetExists1)) {
            if (!buf.hasRemaining()) throw new IOException("Недостаточно данных для чтения ADIO1");
            this.additionalDigitalInputsOctet1 = buf.get();
        }
        if ("1".equals(digitalInputsOctetExists2)) {
            if (!buf.hasRemaining()) throw new IOException("Недостаточно данных для чтения ADIO2");
            this.additionalDigitalInputsOctet2 = buf.get();
        }
        if ("1".equals(digitalInputsOctetExists3)) {
            if (!buf.hasRemaining()) throw new IOException("Недостаточно данных для чтения ADIO3");
            this.additionalDigitalInputsOctet3 = buf.get();
        }
        if ("1".equals(digitalInputsOctetExists4)) {
            if (!buf.hasRemaining()) throw new IOException("Недостаточно данных для чтения ADIO4");
            this.additionalDigitalInputsOctet4 = buf.get();
        }
        if ("1".equals(digitalInputsOctetExists5)) {
            if (!buf.hasRemaining()) throw new IOException("Недостаточно данных для чтения ADIO5");
            this.additionalDigitalInputsOctet5 = buf.get();
        }
        if ("1".equals(digitalInputsOctetExists6)) {
            if (!buf.hasRemaining()) throw new IOException("Недостаточно данных для чтения ADIO6");
            this.additionalDigitalInputsOctet6 = buf.get();
        }
        if ("1".equals(digitalInputsOctetExists7)) {
            if (!buf.hasRemaining()) throw new IOException("Недостаточно данных для чтения ADIO7");
            this.additionalDigitalInputsOctet7 = buf.get();
        }
        if ("1".equals(digitalInputsOctetExists8)) {
            if (!buf.hasRemaining()) throw new IOException("Недостаточно данных для чтения ADIO8");
            this.additionalDigitalInputsOctet8 = buf.get();
        }

        readAnalogSensor(buf, analogSensorFieldExists1, i -> analogSensor1 = i);
        readAnalogSensor(buf, analogSensorFieldExists2, i -> analogSensor2 = i);
        readAnalogSensor(buf, analogSensorFieldExists3, i -> analogSensor3 = i);
        readAnalogSensor(buf, analogSensorFieldExists4, i -> analogSensor4 = i);
        readAnalogSensor(buf, analogSensorFieldExists5, i -> analogSensor5 = i);
        readAnalogSensor(buf, analogSensorFieldExists6, i -> analogSensor6 = i);
        readAnalogSensor(buf, analogSensorFieldExists7, i -> analogSensor7 = i);
        readAnalogSensor(buf, analogSensorFieldExists8, i -> analogSensor8 = i);
    }

    private void readAnalogSensor(ByteBuffer buf, String existsFlag, AnalogSensorConsumer setter) throws IOException {
        if ("1".equals(existsFlag)) {
            if (buf.remaining() < 3) throw new IOException("Недостаточно данных для чтения аналогового датчика");
            int value = (buf.get() & 0xFF) |
                    ((buf.get() & 0xFF) << 8) |
                    ((buf.get() & 0xFF) << 16);
            setter.accept(value);
        }
    }

    @FunctionalInterface
    private interface AnalogSensorConsumer {
        void accept(int value);
    }

    @Override
    public byte[] encode() throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(32);
        buf.order(ByteOrder.LITTLE_ENDIAN);

        int flagBits =
                ("1".equals(digitalInputsOctetExists1) ? 0x01 : 0x00) |
                        ("1".equals(digitalInputsOctetExists2) ? 0x01 : 0x00) << 1 |
                        ("1".equals(digitalInputsOctetExists3) ? 0x01 : 0x00) << 2 |
                        ("1".equals(digitalInputsOctetExists4) ? 0x01 : 0x00) << 3 |
                        ("1".equals(digitalInputsOctetExists5) ? 0x01 : 0x00) << 4 |
                        ("1".equals(digitalInputsOctetExists6) ? 0x01 : 0x00) << 5 |
                        ("1".equals(digitalInputsOctetExists7) ? 0x01 : 0x00) << 6 |
                        ("1".equals(digitalInputsOctetExists8) ? 0x01 : 0x00) << 7;

        buf.put((byte) flagBits);

        buf.put(digitalOutputs);

        flagBits =
                ("1".equals(analogSensorFieldExists1) ? 0x01 : 0x00) << 7 |
                        ("1".equals(analogSensorFieldExists2) ? 0x01 : 0x00) << 6 |
                        ("1".equals(analogSensorFieldExists3) ? 0x01 : 0x00) << 5 |
                        ("1".equals(analogSensorFieldExists4) ? 0x01 : 0x00) << 4 |
                        ("1".equals(analogSensorFieldExists5) ? 0x01 : 0x00) << 3 |
                        ("1".equals(analogSensorFieldExists6) ? 0x01 : 0x00) << 2 |
                        ("1".equals(analogSensorFieldExists7) ? 0x01 : 0x00) << 1 |
                        ("1".equals(analogSensorFieldExists8) ? 0x01 : 0x00);

        buf.put((byte) flagBits);

        if ("1".equals(digitalInputsOctetExists1)) buf.put(additionalDigitalInputsOctet1);
        if ("1".equals(digitalInputsOctetExists2)) buf.put(additionalDigitalInputsOctet2);
        if ("1".equals(digitalInputsOctetExists3)) buf.put(additionalDigitalInputsOctet3);
        if ("1".equals(digitalInputsOctetExists4)) buf.put(additionalDigitalInputsOctet4);
        if ("1".equals(digitalInputsOctetExists5)) buf.put(additionalDigitalInputsOctet5);
        if ("1".equals(digitalInputsOctetExists6)) buf.put(additionalDigitalInputsOctet6);
        if ("1".equals(digitalInputsOctetExists7)) buf.put(additionalDigitalInputsOctet7);
        if ("1".equals(digitalInputsOctetExists8)) buf.put(additionalDigitalInputsOctet8);

        writeAnalogSensor(buf, analogSensorFieldExists1, analogSensor1);
        writeAnalogSensor(buf, analogSensorFieldExists2, analogSensor2);
        writeAnalogSensor(buf, analogSensorFieldExists3, analogSensor3);
        writeAnalogSensor(buf, analogSensorFieldExists4, analogSensor4);
        writeAnalogSensor(buf, analogSensorFieldExists5, analogSensor5);
        writeAnalogSensor(buf, analogSensorFieldExists6, analogSensor6);
        writeAnalogSensor(buf, analogSensorFieldExists7, analogSensor7);
        writeAnalogSensor(buf, analogSensorFieldExists8, analogSensor8);

        byte[] result = new byte[buf.position()];
        buf.rewind();
        buf.get(result);
        return result;
    }

    private void writeAnalogSensor(ByteBuffer buf, String existsFlag, int sensorValue) {
        if ("1".equals(existsFlag)) {
            buf.put((byte) sensorValue);
            buf.put((byte) (sensorValue >>> 8));
            buf.put((byte) (sensorValue >>> 16));
        }
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
