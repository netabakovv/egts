package org.example.libs;

import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс для работы с подзаписью "Данные аналоговых и дискретных датчиков" (SR_AD_SENSORS_DATA)
 * согласно ГОСТ 33472-2015 (EGTS)
 */
@Getter
@Setter
public class SrAdSensorsData implements BinaryData {
    // Флаги наличия дополнительных цифровых входов (1 бит на каждый)
    private boolean digitalInputsOctetExists1;
    private boolean digitalInputsOctetExists2;
    private boolean digitalInputsOctetExists3;
    private boolean digitalInputsOctetExists4;
    private boolean digitalInputsOctetExists5;
    private boolean digitalInputsOctetExists6;
    private boolean digitalInputsOctetExists7;
    private boolean digitalInputsOctetExists8;

    // Основные цифровые выходы (1 байт)
    private byte digitalOutputs;

    // Флаги наличия аналоговых датчиков (1 бит на каждый)
    private boolean analogSensorFieldExists1;
    private boolean analogSensorFieldExists2;
    private boolean analogSensorFieldExists3;
    private boolean analogSensorFieldExists4;
    private boolean analogSensorFieldExists5;
    private boolean analogSensorFieldExists6;
    private boolean analogSensorFieldExists7;
    private boolean analogSensorFieldExists8;

    // Дополнительные цифровые входы (по 1 байту на каждый)
    private byte additionalDigitalInputsOctet1;
    private byte additionalDigitalInputsOctet2;
    private byte additionalDigitalInputsOctet3;
    private byte additionalDigitalInputsOctet4;
    private byte additionalDigitalInputsOctet5;
    private byte additionalDigitalInputsOctet6;
    private byte additionalDigitalInputsOctet7;
    private byte additionalDigitalInputsOctet8;

    // Значения аналоговых датчиков (по 3 байта на каждый)
    private int analogSensor1;
    private int analogSensor2;
    private int analogSensor3;
    private int analogSensor4;
    private int analogSensor5;
    private int analogSensor6;
    private int analogSensor7;
    private int analogSensor8;

    /**
     * Внутренний класс для хранения данных одного аналогового датчика
     */
    @Getter
    @Setter
    public static class AnalogSensor {
        private final int sensorNumber;
        private int value;

        public AnalogSensor(int sensorNumber, int value) {
            this.sensorNumber = sensorNumber;
            this.value = value;
        }
    }

    /**
     * Получить список всех активных аналоговых датчиков
     */
    public List<AnalogSensor> getActiveAnalogSensors() {
        List<AnalogSensor> sensors = new ArrayList<>();
        if (analogSensorFieldExists1) sensors.add(new AnalogSensor(1, analogSensor1));
        if (analogSensorFieldExists2) sensors.add(new AnalogSensor(2, analogSensor2));
        if (analogSensorFieldExists3) sensors.add(new AnalogSensor(3, analogSensor3));
        if (analogSensorFieldExists4) sensors.add(new AnalogSensor(4, analogSensor4));
        if (analogSensorFieldExists5) sensors.add(new AnalogSensor(5, analogSensor5));
        if (analogSensorFieldExists6) sensors.add(new AnalogSensor(6, analogSensor6));
        if (analogSensorFieldExists7) sensors.add(new AnalogSensor(7, analogSensor7));
        if (analogSensorFieldExists8) sensors.add(new AnalogSensor(8, analogSensor8));
        return sensors;
    }

    @Override
    public void decode(byte[] data) throws IOException {
        if (data == null || data.length < 3) {
            throw new IOException("Недостаточно данных для декодирования SR_AD_SENSORS_DATA");
        }

        ByteBuffer buf = ByteBuffer.wrap(data);
        buf.order(ByteOrder.LITTLE_ENDIAN);

        // Чтение флагов цифровых входов
        byte flags = buf.get();
        digitalInputsOctetExists1 = (flags & 0x01) != 0;
        digitalInputsOctetExists2 = (flags & 0x02) != 0;
        digitalInputsOctetExists3 = (flags & 0x04) != 0;
        digitalInputsOctetExists4 = (flags & 0x08) != 0;
        digitalInputsOctetExists5 = (flags & 0x10) != 0;
        digitalInputsOctetExists6 = (flags & 0x20) != 0;
        digitalInputsOctetExists7 = (flags & 0x40) != 0;
        digitalInputsOctetExists8 = (flags & 0x80) != 0;

        // Чтение цифровых выходов
        if (!buf.hasRemaining()) throw new IOException("Недостаточно данных для чтения digitalOutputs");
        digitalOutputs = buf.get();

        // Чтение флагов аналоговых датчиков
        if (!buf.hasRemaining()) throw new IOException("Недостаточно данных для чтения флагов аналоговых датчиков");
        flags = buf.get();
        analogSensorFieldExists1 = (flags & 0x80) != 0;
        analogSensorFieldExists2 = (flags & 0x40) != 0;
        analogSensorFieldExists3 = (flags & 0x20) != 0;
        analogSensorFieldExists4 = (flags & 0x10) != 0;
        analogSensorFieldExists5 = (flags & 0x08) != 0;
        analogSensorFieldExists6 = (flags & 0x04) != 0;
        analogSensorFieldExists7 = (flags & 0x02) != 0;
        analogSensorFieldExists8 = (flags & 0x01) != 0;

        // Чтение дополнительных цифровых входов
        if (digitalInputsOctetExists1) additionalDigitalInputsOctet1 = readOptionalByte(buf, "ADIO1");
        if (digitalInputsOctetExists2) additionalDigitalInputsOctet2 = readOptionalByte(buf, "ADIO2");
        if (digitalInputsOctetExists3) additionalDigitalInputsOctet3 = readOptionalByte(buf, "ADIO3");
        if (digitalInputsOctetExists4) additionalDigitalInputsOctet4 = readOptionalByte(buf, "ADIO4");
        if (digitalInputsOctetExists5) additionalDigitalInputsOctet5 = readOptionalByte(buf, "ADIO5");
        if (digitalInputsOctetExists6) additionalDigitalInputsOctet6 = readOptionalByte(buf, "ADIO6");
        if (digitalInputsOctetExists7) additionalDigitalInputsOctet7 = readOptionalByte(buf, "ADIO7");
        if (digitalInputsOctetExists8) additionalDigitalInputsOctet8 = readOptionalByte(buf, "ADIO8");

        // Чтение аналоговых датчиков
        if (analogSensorFieldExists1) analogSensor1 = readAnalogSensorValue(buf, 1);
        if (analogSensorFieldExists2) analogSensor2 = readAnalogSensorValue(buf, 2);
        if (analogSensorFieldExists3) analogSensor3 = readAnalogSensorValue(buf, 3);
        if (analogSensorFieldExists4) analogSensor4 = readAnalogSensorValue(buf, 4);
        if (analogSensorFieldExists5) analogSensor5 = readAnalogSensorValue(buf, 5);
        if (analogSensorFieldExists6) analogSensor6 = readAnalogSensorValue(buf, 6);
        if (analogSensorFieldExists7) analogSensor7 = readAnalogSensorValue(buf, 7);
        if (analogSensorFieldExists8) analogSensor8 = readAnalogSensorValue(buf, 8);
    }

    private byte readOptionalByte(ByteBuffer buf, String fieldName) throws IOException {
        if (!buf.hasRemaining()) {
            throw new IOException("Недостаточно данных для чтения " + fieldName);
        }
        return buf.get();
    }

    private int readAnalogSensorValue(ByteBuffer buf, int sensorNumber) throws IOException {
        if (buf.remaining() < 3) {
            throw new IOException(String.format("Недостаточно данных для чтения аналогового датчика %d", sensorNumber));
        }
        return ((buf.get() & 0xFF) | (buf.get() & 0xFF) << 8 | (buf.get() & 0xFF) << 16);
    }

    @Override
    public byte[] encode() throws IOException {
        // Вычисляем размер буфера
        int size = 3; // Минимальный размер (флаги + digitalOutputs)

        // Добавляем место для дополнительных цифровых входов
        size += countActiveDigitalInputs();

        // Добавляем место для аналоговых датчиков
        size += countActiveAnalogSensors() * 3;

        ByteBuffer buf = ByteBuffer.allocate(size);
        buf.order(ByteOrder.LITTLE_ENDIAN);

        // Запись флагов цифровых входов
        byte flags = (byte) (
                (digitalInputsOctetExists1 ? 0x01 : 0) |
                        (digitalInputsOctetExists2 ? 0x02 : 0) |
                        (digitalInputsOctetExists3 ? 0x04 : 0) |
                        (digitalInputsOctetExists4 ? 0x08 : 0) |
                        (digitalInputsOctetExists5 ? 0x10 : 0) |
                        (digitalInputsOctetExists6 ? 0x20 : 0) |
                        (digitalInputsOctetExists7 ? 0x40 : 0) |
                        (digitalInputsOctetExists8 ? 0x80 : 0)
        );
        buf.put(flags);

        // Запись цифровых выходов
        buf.put(digitalOutputs);

        // Запись флагов аналоговых датчиков
        flags = (byte) (
                (analogSensorFieldExists1 ? 0x80 : 0) |
                        (analogSensorFieldExists2 ? 0x40 : 0) |
                        (analogSensorFieldExists3 ? 0x20 : 0) |
                        (analogSensorFieldExists4 ? 0x10 : 0) |
                        (analogSensorFieldExists5 ? 0x08 : 0) |
                        (analogSensorFieldExists6 ? 0x04 : 0) |
                        (analogSensorFieldExists7 ? 0x02 : 0) |
                        (analogSensorFieldExists8 ? 0x01 : 0)
        );
        buf.put(flags);

        // Запись дополнительных цифровых входов
        if (digitalInputsOctetExists1) buf.put(additionalDigitalInputsOctet1);
        if (digitalInputsOctetExists2) buf.put(additionalDigitalInputsOctet2);
        if (digitalInputsOctetExists3) buf.put(additionalDigitalInputsOctet3);
        if (digitalInputsOctetExists4) buf.put(additionalDigitalInputsOctet4);
        if (digitalInputsOctetExists5) buf.put(additionalDigitalInputsOctet5);
        if (digitalInputsOctetExists6) buf.put(additionalDigitalInputsOctet6);
        if (digitalInputsOctetExists7) buf.put(additionalDigitalInputsOctet7);
        if (digitalInputsOctetExists8) buf.put(additionalDigitalInputsOctet8);

        // Запись аналоговых датчиков
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

    private int countActiveDigitalInputs() {
        int count = 0;
        if (digitalInputsOctetExists1) count++;
        if (digitalInputsOctetExists2) count++;
        if (digitalInputsOctetExists3) count++;
        if (digitalInputsOctetExists4) count++;
        if (digitalInputsOctetExists5) count++;
        if (digitalInputsOctetExists6) count++;
        if (digitalInputsOctetExists7) count++;
        if (digitalInputsOctetExists8) count++;
        return count;
    }

    private int countActiveAnalogSensors() {
        int count = 0;
        if (analogSensorFieldExists1) count++;
        if (analogSensorFieldExists2) count++;
        if (analogSensorFieldExists3) count++;
        if (analogSensorFieldExists4) count++;
        if (analogSensorFieldExists5) count++;
        if (analogSensorFieldExists6) count++;
        if (analogSensorFieldExists7) count++;
        if (analogSensorFieldExists8) count++;
        return count;
    }

    private void writeAnalogSensor(ByteBuffer buf, boolean exists, int value) {
        if (exists) {
            buf.put((byte) (value & 0xFF));
            buf.put((byte) ((value >> 8) & 0xFF));
            buf.put((byte) ((value >> 16) & 0xFF));
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