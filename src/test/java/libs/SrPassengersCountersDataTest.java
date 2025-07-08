package libs;

import org.example.libs.PassengersCounter;
import org.example.libs.SrPassengersCountersData;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class SrPassengersCountersDataTest {

    @Test
    void testEncodeDecode_withRawDataFlag() throws IOException {
        SrPassengersCountersData original = new SrPassengersCountersData();
        original.setRawDataFlag("1");
        original.setDoorsPresented("00000000");
        original.setDoorsReleased("00000000");
        original.setModuleAddress(12345);
        original.setPassengersCountersRawData(new byte[]{0x01, 0x02, 0x03});

        byte[] encoded = original.encode();

        SrPassengersCountersData decoded = new SrPassengersCountersData();
        decoded.decode(encoded);

        assertEquals("1", decoded.getRawDataFlag());
        assertEquals("00000000", decoded.getDoorsPresented());
        assertEquals("00000000", decoded.getDoorsReleased());
        assertEquals(12345, decoded.getModuleAddress());
        assertArrayEquals(new byte[]{0x01, 0x02, 0x03}, decoded.getPassengersCountersRawData());
    }


    @Test
    void testEncodeDecode_withPassengersCountersData() throws IOException {
        SrPassengersCountersData original = new SrPassengersCountersData();
        original.setRawDataFlag("0");
        original.setDoorsPresented("00010000"); // Only door 5 active
        original.setDoorsReleased("00000000");
        original.setModuleAddress(42);

        PassengersCounter counter = new PassengersCounter((byte) 5, (byte) 10, (byte) 20);
        original.setPassengersCountersData(Collections.singletonList(counter));

        byte[] encoded = original.encode();

        SrPassengersCountersData decoded = new SrPassengersCountersData();
        decoded.decode(encoded);

        assertEquals("0", decoded.getRawDataFlag());
        assertEquals("00010000", decoded.getDoorsPresented());
        assertEquals("00000000", decoded.getDoorsReleased());
        assertEquals(42, decoded.getModuleAddress());

        assertEquals(1, decoded.getPassengersCountersData().size());
        PassengersCounter decodedCounter = decoded.getPassengersCountersData().get(0);
        assertEquals(5, decodedCounter.getDoorNo());
        assertEquals(10, decodedCounter.getIn());
        assertEquals(20, decodedCounter.getOut());
    }

    @Test
    void testEncode_withInvalidFlags_throwsException() {
        SrPassengersCountersData data = new SrPassengersCountersData();
        data.setRawDataFlag("2"); // invalid bit
        data.setDoorsPresented("00000000");
        data.setDoorsReleased("00000000");
        data.setModuleAddress(1);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, data::encode);
        assertTrue(ex.getMessage().contains("Флаг содержит недопустимый символ"));
    }

    @Test
    void testDecode_withInsufficientContent_throwsException() {
        SrPassengersCountersData data = new SrPassengersCountersData();
        byte[] tooShort = new byte[3]; // must be at least 5

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> data.decode(tooShort));
        assertTrue(ex.getMessage().contains("Недостаточно данных"));
    }

    @Test
    void testDecode_withMissingPassengerData_throwsException() {
        SrPassengersCountersData data = new SrPassengersCountersData();

        // rawDataFlag = 0, one door active but no counters
        byte[] encoded = new byte[]{
                0b00000000, // flags (rawDataFlag = 0)
                0b00010000, // doorsPresented: door 5
                0b00000000, // doorsReleased
                0x2A, 0x00  // moduleAddress = 42
                // no in/out data
        };

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> data.decode(encoded));
        assertTrue(ex.getMessage().contains("Недостаточно данных для чтения In/Out двери #5"));
    }

    @Test
    void testLengthMatchesEncode() throws IOException {
        SrPassengersCountersData data = new SrPassengersCountersData();
        data.setRawDataFlag("1");
        data.setDoorsPresented("11110000");
        data.setDoorsReleased("00001111");
        data.setModuleAddress(999);
        data.setPassengersCountersRawData(new byte[]{0x01, 0x02});

        int expectedLength = data.encode().length;
        assertEquals(expectedLength, data.length());
    }
}
