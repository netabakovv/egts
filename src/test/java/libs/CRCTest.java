package libs;

import org.example.libs.CRC;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CRCTest {
    @Test
    void test_crc8() {
        String input = "123456789";
        byte actual = CRC.crc8(input.getBytes());
        byte expected = (byte) 0xF7;

        assertEquals(expected, actual, "CRC8 должен быть равен 0xF7 для строки '123456789'");
    }

    @Test
    void test_crc16() {
        String input = "123456789";
        short actual = CRC.crc16(input.getBytes());
        short expected = (short) 0x29B1;

        assertEquals(expected, actual, "CRC16 должен быть равен 0x29B1 для строки '123456789'");
    }
}
