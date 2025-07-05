package libs;

import org.example.libs.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.example.libs.RecordDataSet.AuthService;
import static org.example.libs.RecordDataSet.SrResultCodeType;
import static org.junit.jupiter.api.Assertions.*;

public class SrResultCodeTest {

    byte[] testEgtsPkgSrResCodeBytes = new byte[]{
            0x01, 0x00, 0x00, 0x0B, 0x00, 0x0B, 0x00, 0x15, 0x38, 0x01, 0x11, 0x04, 0x00,
            0x15, 0x38, 0x20, 0x01, 0x01, 0x09, 0x01, 0x00, 0x00, 0x3C, (byte) 0xBC
    };

    private EgtsPackage getExpected() {
        EgtsPackage pkg = new EgtsPackage();
        pkg.setProtocolVersion((byte) 1);
        pkg.setSecurityKeyId((byte) 0);
        pkg.setPrefix("00");
        pkg.setRoute("0");
        pkg.setEncryptionAlg("00");
        pkg.setCompression("0");
        pkg.setPriority("00");
        pkg.setHeaderLength((byte) 11);
        pkg.setHeaderEncoding((byte) 0);
        pkg.setFrameDataLength((short) 11);
        pkg.setPacketIdentifier((short) 14357);
        pkg.setPacketType(EgtsPacketType.PT_APP_DATA);
        pkg.setHeaderCheckSum((byte) 17);

        ServiceDataSet sds = new ServiceDataSet();
        ServiceDataRecord sdr = new ServiceDataRecord();
        sdr.setRecordLength((short) 4);
        sdr.setRecordNumber((short) 14357);
        sdr.setSourceServiceOnDevice("0");
        sdr.setRecipientServiceOnDevice("0");
        sdr.setGroup("1");
        sdr.setRecordProcessingPriority("00");
        sdr.setTimeFieldExists("0");
        sdr.setEventIDFieldExists("0");
        sdr.setObjectIDFieldExists("0");
        sdr.setSourceServiceType(AuthService);
        sdr.setRecipientServiceType(AuthService);

        RecordDataSet rds = new RecordDataSet();
        RecordDataSet.RecordData recordData = new RecordDataSet.RecordData();
        recordData.setSubrecordType(SrResultCodeType);
        recordData.setSubrecordLength((short) 1);

        SrResultCode srResultCode = new SrResultCode();
        srResultCode.setResultCode((byte) EgtsProcessingCode.OK.getCode());

        recordData.setSubrecordData(srResultCode);
        rds.addRecord(recordData);
        sdr.setRecordDataSet(rds);
        sds.addRecord(sdr);
        pkg.setServicesFrameData(sds);
        pkg.setServicesFrameDataCheckSum((short) 48188);

        return pkg;
    }

    @Test
    public void testEncode() throws IOException {
        EgtsPackage egtsPackage = getExpected();
        byte[] encoded = egtsPackage.encode();

        assertArrayEquals(encoded, testEgtsPkgSrResCodeBytes);
    }

    @Test
    public void testDecode() throws IOException {
        EgtsPackage egtsPackage = new EgtsPackage();
        egtsPackage.decode(testEgtsPkgSrResCodeBytes);

        EgtsPackage expected = getExpected();

        assertEquals(egtsPackage, expected);
    }
 }
