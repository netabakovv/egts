package libs;

import org.junit.jupiter.api.Test;
import org.example.libs.SrAuthInfo;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class SrAuthInfoTest {

    private static final byte[] srAuthInfoPkgBytes = new byte[]{
            '8', '0', '0', 0x00,
            'E', 'F', '2', '8', '4', 'E', '7', 'A', 'E', '3', '5', '1',
            'D', '6', 'D', 'F', '9', '2', 'C', 'E', '3', '2', '3', 'D',
            '7', '4', 'A', 'D', '2', 'E', 'B', '3', 0x00
    };

    @Test
    public void testEncode() throws IOException {
        SrAuthInfo authInfo = new SrAuthInfo();
        authInfo.setUserName("800");
        authInfo.setUserPassword("EF284E7AE351D6DF92CE323D74AD2EB3");

        byte[] encoded = authInfo.encode();

        assertNotNull(encoded);
        assertArrayEquals(srAuthInfoPkgBytes, encoded);
    }

    @Test
    public void testDecode() throws IOException {
        SrAuthInfo decoded = new SrAuthInfo();
        decoded.decode(srAuthInfoPkgBytes);

        assertEquals("800", decoded.getUserName());
        assertEquals("EF284E7AE351D6DF92CE323D74AD2EB3", decoded.getUserPassword());
        assertNull(decoded.getServerSequence());
    }
}