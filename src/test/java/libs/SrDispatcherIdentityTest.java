package libs;

import org.example.libs.SrDispatcherIdentity;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static org.junit.jupiter.api.Assertions.*;

class SrDispatcherIdentityTest {

    @Test
    void testEncode() throws IOException {
        byte dispatcherType = 1;
        int dispatcherID = 123456789;
        String description = "Test";

        SrDispatcherIdentity identity = new SrDispatcherIdentity(dispatcherType, dispatcherID, description);
        byte[] encoded = identity.encode();

        ByteBuffer buf = ByteBuffer.wrap(encoded).order(ByteOrder.LITTLE_ENDIAN);

        assertEquals(dispatcherType, buf.get());
        assertEquals(dispatcherID, buf.getInt());

        byte[] descBytes = new byte[encoded.length - 5];
        buf.get(descBytes);
        assertEquals(description, new String(descBytes));
    }


    @Test
    void testDecode() throws IOException {
        // dispatcherType = 2
        byte dispatcherType = 2;
        int dispatcherID = 987654321;
        String description = "Hello";
        byte[] descBytes = description.getBytes();

        ByteBuffer buf = ByteBuffer.allocate(1 + 4 + descBytes.length);
        buf.put(dispatcherType);

        // Пишем dispatcherID в little-endian
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.putInt(dispatcherID);

        buf.put(descBytes);
        byte[] bytes = buf.array();

        SrDispatcherIdentity identity = new SrDispatcherIdentity();
        identity.decode(bytes);

        assertEquals(dispatcherType, identity.getDispatcherType());
        assertEquals(dispatcherID, identity.getDispatcherID());
        assertEquals(description, identity.getDescription());
    }


    @Test
    void testEncodeDecodeReversibility() throws IOException {
        SrDispatcherIdentity original = new SrDispatcherIdentity((byte) 10, 555, "Dispatcher-X");
        byte[] encoded = original.encode();

        SrDispatcherIdentity decoded = new SrDispatcherIdentity();
        decoded.decode(encoded);

        assertEquals(original, decoded);
    }

    @Test
    void testLength() {
        SrDispatcherIdentity identity = new SrDispatcherIdentity((byte) 3, 100, "Desc");
        int expectedLength = 1 + 4 + "Desc".getBytes().length;

        assertEquals(expectedLength, identity.length());
    }

    @Test
    void testDecodeEmptyDescription() throws IOException {
        byte dispatcherType = 1;
        int dispatcherID = 42;

        ByteBuffer buf = ByteBuffer.allocate(5).order(ByteOrder.LITTLE_ENDIAN);
        buf.put(dispatcherType);
        buf.putInt(dispatcherID);

        SrDispatcherIdentity identity = new SrDispatcherIdentity();
        identity.decode(buf.array());

        assertEquals(dispatcherType, identity.getDispatcherType());
        assertEquals(dispatcherID, identity.getDispatcherID());
        assertEquals("", identity.getDescription());
    }


    @Test
    void testDecodeInvalidLengthThrows() {
        byte[] invalid = new byte[2]; // слишком короткий

        SrDispatcherIdentity identity = new SrDispatcherIdentity();

        assertThrows(IllegalArgumentException.class, () -> identity.decode(invalid));
    }

    @Test
    void testDecodeNullThrows() {
        SrDispatcherIdentity identity = new SrDispatcherIdentity();

        assertThrows(IllegalArgumentException.class, () -> identity.decode(null));
    }
}
