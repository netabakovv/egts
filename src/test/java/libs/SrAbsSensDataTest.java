package libs;

import org.example.libs.SrAbsSensData;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Fail.fail;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class SrAbsSensDataTest {
    @Test
    void test_encode_abs_an_sens_data() throws IOException {
        SrAbsSensData a = new SrAbsSensData();
        a.setSensorNumber((byte) 0x98);
        a.setValue(0x123456); // 0x123456 → байты: 0x56, 0x34, 0x12

        byte[] data = a.encode();

        assertArrayEquals(new byte[]{
                (byte) 0x98,
                (byte) 0x56,
                (byte) 0x34,
                (byte) 0x12
        }, data);

        SrAbsSensData b = new SrAbsSensData();
        b.decode(data);

        assertEquals(a.getSensorNumber(), b.getSensorNumber());
        assertEquals(a.getValue(), b.getValue());
    }

    @Test
    void test_decode_abs_an_sens_data() {
        byte[] data = new byte[]{(byte) 0x98, (byte) 0x56, (byte) 0x34, (byte) 0x12};

        SrAbsSensData a = new SrAbsSensData();
        try {
            a.decode(data);
        } catch (IOException e) {
            fail("Decode не должен выбрасывать исключение", e);
        }

        assertEquals(0x98, a.getSensorNumber() & 0xFF); // беззнаковое значение
        assertEquals(0x123456, a.getValue());

        byte[] data2;
        try {
            data2 = a.encode();
        } catch (IOException e) {
            fail("Encode не должен выбрасывать исключение", e);
            return;
        }

        assertArrayEquals(data, data2);
    }
}
