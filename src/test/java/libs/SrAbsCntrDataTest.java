package libs;

import org.example.libs.SrAbsCntrData;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class SrAbsCntrDataTest {
    private static final byte[] SR_ABS_CNTR_DATA_BYTES = new byte[]{
            (byte) 0x06, (byte) 0x75, (byte) 0x1D, (byte) 0x70
    };

    private static final int COUNTER_NUMBER = 0x06; // 6
    private static final int COUNTER_VALUE = 0x701D75; // LittleEndian: 0x75, 0x1D, 0x70 → 0x701D75

    @Test
    void test_encode_abs_cntr_data() throws IOException {
        SrAbsCntrData data = new SrAbsCntrData();
        data.setCounterNumber((byte) COUNTER_NUMBER);
        data.setCounterValue(COUNTER_VALUE);

        byte[] encoded = data.encode();

        assertArrayEquals(SR_ABS_CNTR_DATA_BYTES, encoded);
    }

    @Test
    void test_decode_abs_cntr_data() throws IOException {
        SrAbsCntrData decoded = new SrAbsCntrData();
        decoded.decode(SR_ABS_CNTR_DATA_BYTES);

        assertEquals(COUNTER_NUMBER, decoded.getCounterNumber() & 0xFF); // беззнаковое значение
        assertEquals(COUNTER_VALUE, decoded.getCounterValue());
    }
}
