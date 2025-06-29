package org.example.libs;

public class CRC {
    public static byte crc8(byte[] data) {
        byte crc = (byte) 0xFF;
        for (byte b : data) {
            crc ^= b;

            for (int i = 0; i < 8; i++) {
                if ((crc & 0x80) != 0) {
                    crc = (byte) ((crc << 1) ^ 0x31);
                } else {
                    crc <<= 1;
                }
            }
        }
        return crc;
    }

    public static short crc16(byte[] data) {
        int crc = 0xFFFF;
        for (byte b : data) {
            crc ^= ((b & 0xFF) << 8);

            for (int i = 0; i < 8; i++) {
                if ((crc & 0x8000) != 0) {
                    crc = (crc << 1) ^ 0x1021;
                } else {
                    crc <<= 1;
                }
            }
        }
        return (short) (crc & 0xFFFF);
    }
}
