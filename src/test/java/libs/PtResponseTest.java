package libs;

import org.example.libs.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class PtResponseTest {
    private static final byte[] TEST_EGTS_PKG_BYTES = new byte[]{
            0x01, 0x00, 0x03, 0x0B, 0x00, 0x03, 0x00, (byte) 0x89,
            0x00, 0x00, 0x4A, 0x15, 0x38, 0x00, 0x33, (byte) 0xE8
    };

    @Test
    void test_encode_pt_response() throws IOException {
        // Создаем PtResponse
        PtResponse ptResponse = new PtResponse();
        ptResponse.setResponsePacketID((short) 14357);
        ptResponse.setProcessingResult((byte) 0x00);

        EgtsPackage egtsPkg = new EgtsPackage();
        egtsPkg.setProtocolVersion((byte) 1);
        egtsPkg.setSecurityKeyId((byte) 0);
        egtsPkg.setPrefix("00");
        egtsPkg.setRoute("0");
        egtsPkg.setEncryptionAlg("00");
        egtsPkg.setCompression("0");
        egtsPkg.setPriority("11");
        egtsPkg.setHeaderLength((byte) 11);
        egtsPkg.setHeaderEncoding((byte) 0);
        egtsPkg.setFrameDataLength((short) 3);
        egtsPkg.setPacketIdentifier((short) 137);
        egtsPkg.setPacketType(EgtsPacketType.PT_RESPONSE);
        egtsPkg.setHeaderCheckSum((byte) 74);
        egtsPkg.setServicesFrameData(ptResponse);

        byte[] encoded = egtsPkg.encode();

        assertArrayEquals(TEST_EGTS_PKG_BYTES, encoded);
    }

    @Test
    void test_decode_pt_response() throws IOException {
        EgtsPackage decoded = new EgtsPackage();
        EgtsPackage.DecodeResult result = decoded.decode(TEST_EGTS_PKG_BYTES);

        assertEquals(EgtsPackage.DecodeResultCode.OK, result.code());

        EgtsPackage pkg = result.pkg();

        assertEquals(1, pkg.getProtocolVersion());
        assertEquals(0, pkg.getSecurityKeyId());
        assertEquals("00", pkg.getPrefix());
        assertEquals("0", pkg.getRoute());
        assertEquals("00", pkg.getEncryptionAlg());
        assertEquals("0", pkg.getCompression());
        assertEquals("11", pkg.getPriority());
        assertEquals(11, pkg.getHeaderLength());
        assertEquals(0, pkg.getHeaderEncoding());
        assertEquals(3, pkg.getFrameDataLength());
        assertEquals(137, pkg.getPacketIdentifier());
        assertEquals(EgtsPacketType.PT_RESPONSE, pkg.getPacketType());
        assertEquals(74, pkg.getHeaderCheckSum() & 0xFF); // беззнаковое значение

        assertNotNull(pkg.getServicesFrameData(), "Sdr не должен быть null");

        PtResponse response = (PtResponse) pkg.getServicesFrameData();
        assertEquals(14357, response.getResponsePacketID());
        assertEquals((byte) 0x00, response.getProcessingResult());
        assertNull(response.getSdr(), "Внутренний SDR должен быть null");
    }
}