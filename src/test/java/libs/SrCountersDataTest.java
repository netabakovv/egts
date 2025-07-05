package libs;

import org.example.libs.RecordDataSet;
import org.example.libs.SrCountersData;
import org.junit.jupiter.api.Test;

import static org.example.libs.RecordDataSet.SrCountersDataType;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.List;

public class SrCountersDataTest {
    SrCountersData testEgtsSrCountersData = new SrCountersData(
            "0",
            "0",
            "0",
            "0",
            "0",
            "0",
            "1",
            "1",
            0,
            0,
            0,
            0,
            0,
            0,
            0,
            3
    );
    byte[] testSrCountersDataBytes = {(byte) 0xC0, 0x00, 0x00, 0x00, 0x03, 0x00, 0x00};


    @Test
    public void testEncode() throws IOException {
        byte[] counterBytes = testEgtsSrCountersData.encode();
        assertArrayEquals(counterBytes, testSrCountersDataBytes);
    }

    @Test
    public void testDecode() throws IOException {
        SrCountersData decoded = new SrCountersData();
        decoded.decode(testSrCountersDataBytes);

        assertEquals("1", decoded.getCounterFieldExists1());
        assertEquals("1", decoded.getCounterFieldExists2());
        assertEquals("0", decoded.getCounterFieldExists3());
        assertEquals("0", decoded.getCounterFieldExists4());
        assertEquals("0", decoded.getCounterFieldExists5());
        assertEquals("0", decoded.getCounterFieldExists6());
        assertEquals("0", decoded.getCounterFieldExists7());
        assertEquals("0", decoded.getCounterFieldExists8());

        assertEquals(0, decoded.getCounter1());
        assertEquals(3, decoded.getCounter2());
        assertEquals(0, decoded.getCounter3());
        assertEquals(0, decoded.getCounter4());
        assertEquals(0, decoded.getCounter5());
        assertEquals(0, decoded.getCounter6());
        assertEquals(0, decoded.getCounter7());
        assertEquals(0, decoded.getCounter8());
    }
}