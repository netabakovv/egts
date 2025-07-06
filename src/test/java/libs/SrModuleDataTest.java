package libs;

import org.example.libs.SrModuleData;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class SrModuleDataTest {

    @Test
    void testEncode() throws IOException {
        SrModuleData sr = new SrModuleData();
        sr.setModuleType((byte) 0x10);
        sr.setVendorID(0x11223344);
        sr.setFirmwareVersion((short) 0x5566);
        sr.setSoftwareVersion((short) 0x7788);
        sr.setModification((byte) 0x99);
        sr.setState((byte) 0xAA);
        sr.setSerialNumber("SN");
        sr.setDescription("DESC");

        byte[] encoded = sr.encode();

        assertNotNull(encoded);
        assertTrue(encoded.length > 14);

        // Кол-во фиксированных байт в начале
        int fixedSize = 1 + 4 + 2 + 2 + 1 + 1; // 11

        // delimiter после SerialNumber
        int delimiterAfterSNIndex = fixedSize + sr.getSerialNumber().getBytes(StandardCharsets.UTF_8).length;
        assertEquals(0x00, encoded[delimiterAfterSNIndex], "Delimiter after SerialNumber missing");

        // delimiter после Description
        int delimiterAfterDescIndex = delimiterAfterSNIndex + 1 + sr.getDescription().getBytes(StandardCharsets.UTF_8).length;
        assertEquals(0x00, encoded[delimiterAfterDescIndex], "Delimiter after Description missing");
    }


    @Test
    void testDecode() throws IOException {
        byte[] data = new byte[] {
                0x10,                          // moduleType
                0x44, 0x33, 0x22, 0x11,       // vendorID (0x11223344 little-endian)
                0x66, 0x55,                   // firmwareVersion (0x5566 little-endian)
                (byte)0x88, 0x77,             // softwareVersion (0x7788 little-endian)
                (byte)0x99,                   // modification
                (byte)0xAA,                   // state
                'S', 'N', 0x00,               // serialNumber + delimiter
                'D', 'E', 'S', 'C', 0x00      // description + delimiter
        };

        SrModuleData sr = new SrModuleData();
        sr.decode(data);

        assertEquals((byte) 0x10, sr.getModuleType());
        assertEquals(0x11223344, sr.getVendorID());
        assertEquals((short) 0x5566, sr.getFirmwareVersion());
        assertEquals((short) 0x7788, sr.getSoftwareVersion());
        assertEquals((byte) 0x99, sr.getModification());
        assertEquals((byte) 0xAA, sr.getState());
        assertEquals("SN", sr.getSerialNumber());
        assertEquals("DESC", sr.getDescription());
    }

    @Test
    void testEncodeDecodeTogether() throws IOException {
        SrModuleData original = new SrModuleData(
                (byte) 0x01,
                0x12345678,
                (short) 0x1122,
                (short) 0x3344,
                (byte) 0x55,
                (byte) 0x66,
                "SERIAL",
                "DESCRIPTION"
        );

        byte[] encoded = original.encode();

        SrModuleData decoded = new SrModuleData();
        decoded.decode(encoded);

        assertEquals(original.getModuleType(), decoded.getModuleType());
        assertEquals(original.getVendorID(), decoded.getVendorID());
        assertEquals(original.getFirmwareVersion(), decoded.getFirmwareVersion());
        assertEquals(original.getSoftwareVersion(), decoded.getSoftwareVersion());
        assertEquals(original.getModification(), decoded.getModification());
        assertEquals(original.getState(), decoded.getState());
        assertEquals(original.getSerialNumber(), decoded.getSerialNumber());
        assertEquals(original.getDescription(), decoded.getDescription());
    }
}
