package org.example.libs;

import lombok.Getter;
import lombok.Setter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Getter
@Setter
public class SrStateData implements BinaryData{
    private byte state;
    private byte mainPowerSourceVoltage;
    private byte backUpBatteryVoltage;
    private byte internalBatteryVoltage;

    // биты флагов, хранимые как строки 0 или 1
    private String NMS = "0";
    private String IBU = "0";
    private String BBU = "0";

    @Override
    public void decode(byte[] data) throws Exception {
        if (data == null || data.length < 5) {
            throw new IOException("Недостаточно данных для SrStateData");
        }

        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        setState((byte) bais.read());
        setMainPowerSourceVoltage((byte) bais.read());
        setBackUpBatteryVoltage((byte) bais.read());
        setInternalBatteryVoltage((byte) bais.read());

        int flags = bais.read();
        if (flags == -1) {
            throw new IOException("Не удалось прочитать байт флагов");
        }

        String flagBits = String.format("%8s", Integer.toBinaryString(flags & 0xFF)).replace(' ', '0');

        setNMS(flagBits.substring(5, 6));
        setIBU(flagBits.substring(6, 7));
        setBBU(flagBits.substring(7));
    }

    @Override
    public byte[] encode() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        baos.write(state);
        baos.write(mainPowerSourceVoltage);
        baos.write(backUpBatteryVoltage);
        baos.write(internalBatteryVoltage);

        // Собираем флаги: "00000" + NMS + IBU + BBU
        String bitString = "00000" + NMS + IBU + BBU;
        try {
            int flagByte = Integer.parseInt(bitString, 2);
            baos.write(flagByte);
        } catch (NumberFormatException e) {
            throw new IOException("Некорректный формат битов флагов: " + bitString, e);
        }

        return baos.toByteArray();
    }

    @Override
    public short length() {
        return 5; // Всегда 5 байт (4 поля по 1 + 1 байт флагов)
    }

    @Override
    public String toString() {
        return "SrStateData{" +
                "state=" + (state & 0xFF) +
                ", MPSV=" + (mainPowerSourceVoltage & 0xFF) +
                ", BBV=" + (backUpBatteryVoltage & 0xFF) +
                ", IBV=" + (internalBatteryVoltage & 0xFF) +
                ", NMS=" + NMS +
                ", IBU=" + IBU +
                ", BBU=" + BBU +
                '}';
    }
}
