package libs;

import org.example.libs.ServiceDataRecord;
import org.example.libs.ServiceDataSet;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceDataRecordsTest {

    @Test
    public void testEncode() throws Exception {
        ServiceDataSet testServiceDataRecord = new ServiceDataSet();

        ServiceDataRecord testDR = new ServiceDataRecord();
        testDR.setRecordLength((short) 0);
        testDR.setRecordNumber((short) 97);
        testDR.setSourceServiceOnDevice("1");
        testDR.setRecipientServiceOnDevice("0");
        testDR.setGroup("0");
        testDR.setRecordProcessingPriority("11");
        testDR.setTimeFieldExists("0");
        testDR.setEventIDFieldExists("0");
        testDR.setObjectIDFieldExists("1");
        testDR.setObjectIdentifier(133552);
        testDR.setSourceServiceType((byte) 2);
        testDR.setRecipientServiceType((byte) 2);

        testServiceDataRecord.addRecord(testDR);

        byte[] testServiceDataRecordBytes = new byte[]{0x00, 0x00, 0x61, 0x00, (byte) 0x99, (byte) 0xB0, 0x09, 0x02, 0x00, 0x02, 0x02};

        byte[] encoded = testServiceDataRecord.encode();

        assertArrayEquals(encoded, testServiceDataRecordBytes);
    }

    @Test
    public void testDecode() throws Exception {
        byte[] testServiceDataRecordBytes = new byte[]{
                0x00, 0x00, 0x61, 0x00, (byte) 0x99, (byte) 0xB0, 0x09, 0x02, 0x00, 0x02, 0x02
        };

        ServiceDataSet data = new ServiceDataSet();
        data.decode(testServiceDataRecordBytes);

        ServiceDataSet testServiceDataRecord = new ServiceDataSet();
        ServiceDataRecord sdr = new ServiceDataRecord();
        sdr.setRecordLength((short) 0);
        sdr.setRecordNumber((short) 97);
        sdr.setSourceServiceOnDevice("1");
        sdr.setRecipientServiceOnDevice("0");
        sdr.setGroup("0");
        sdr.setRecordProcessingPriority("11");
        sdr.setTimeFieldExists("0");
        sdr.setEventIDFieldExists("0");
        sdr.setObjectIDFieldExists("1");
        sdr.setObjectIdentifier(133552);
        sdr.setSourceServiceType((byte) 2);
        sdr.setRecipientServiceType((byte) 2);

        testServiceDataRecord.addRecord(sdr);

        assertEquals(data, testServiceDataRecord);
    }
}
