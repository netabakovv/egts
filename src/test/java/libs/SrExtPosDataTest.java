package libs;
import org.example.libs.SrExtPosData;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.junit.jupiter.api.Assertions.*;

class SrExtPosDataTest {

    @Test
    void testEncodeDecode_AllFieldsPresent() throws IOException {
        SrExtPosData original = new SrExtPosData();
        original.setVdopFieldExists("1");
        original.setHdopFieldExists("1");
        original.setPdopFieldExists("1");
        original.setSatellitesFieldExists("1");
        original.setNavigationSystemFieldExists("1");

        original.setVdop(1234);   // 0x04D2
        original.setHdop(2345); // 0x0929
        original.setPdop(3456);   // 0x0D80
        original.setSatellites((byte) 12);
        original.setNavigationSystem(2); // GPS

        byte[] encoded = original.encode();

        SrExtPosData decoded = new SrExtPosData();
        decoded.decode(encoded);

        assertEquals("1", decoded.getVdopFieldExists());
        assertEquals("1", decoded.getHdopFieldExists());
        assertEquals("1", decoded.getPdopFieldExists());
        assertEquals("1", decoded.getSatellitesFieldExists());
        assertEquals("1", decoded.getNavigationSystemFieldExists());

        assertEquals(1234, decoded.getVdop());
        assertEquals(2345, decoded.getHdop());
        assertEquals(3456, decoded.getPdop());
        assertEquals(12, decoded.getSatellites());
        assertEquals(2, decoded.getNavigationSystem());
    }

    @Test
    void testDecode_InsufficientData() {
        SrExtPosData data = new SrExtPosData();
        byte[] input = new byte[]{(byte) 0b11111000}; // все флаги, но без полей

        assertThrows(IllegalArgumentException.class, () -> data.decode(input));
    }
}
