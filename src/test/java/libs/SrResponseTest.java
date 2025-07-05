package libs;

import org.example.libs.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.example.libs.RecordDataSet.*;
import static org.junit.jupiter.api.Assertions.*;

public class SrResponseTest {
    byte[] testEgtsPkgSrRespBytes = new byte[]{
            0x01, 0x00, 0x00, 0x0B, 0x00, 0x10, 0x00, (byte) 0x86, 0x00, 0x00, 0x18, (byte) 0x86, 0x00, 0x00,
            0x06, 0x00, 0x5F, 0x00, 0x20, 0x01, 0x01, 0x00, 0x03, 0x00, 0x5F, 0x00, 0x00, 0x13, 0x73
    };

    private EgtsPackage getExpected() throws IOException {
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
        pkg.setFrameDataLength((short) 16);
        pkg.setPacketIdentifier((short) 134);
        pkg.setPacketType(EgtsPacketType.PT_RESPONSE);
        pkg.setHeaderCheckSum((byte) 24);

        PtResponse ptResponse = new PtResponse();
        ptResponse.setResponsePacketID((short) 134);
        ptResponse.setProcessingResult((byte) 0);

        ServiceDataSet sds = new ServiceDataSet();

        ServiceDataRecord sdr = new ServiceDataRecord();
        sdr.setRecordLength((short) 6);
        sdr.setRecordNumber((short) 95);
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
        recordData.setSubrecordType(SrRecordResponseType);
        recordData.setSubrecordLength((short) 3);

        SrResponse srResponse = new SrResponse();
        srResponse.setConfirmedRecordNumber(95);
        srResponse.setRecordStatus(EgtsProcessingCode.OK.getCode());

        recordData.setSubrecordData(srResponse);
        rds.addRecord(recordData);
        sdr.setRecordDataSet(rds);
        sds.addRecord(sdr);
        ptResponse.setSdr(sds);
        pkg.setServicesFrameData(ptResponse);

        // Вычисляем ServicesFrameDataCheckSum по закодированным данным ServiceFrameData
        byte[] sfrdBytes = ptResponse.encode();
        short crc16 = CRC.crc16(sfrdBytes);
        pkg.setServicesFrameDataCheckSum(crc16);

        return pkg;
    }

    @Test
    public void testEncode() throws Exception {
        EgtsPackage pkg = getExpected();
        byte[] encoded = pkg.encode();

        assertArrayEquals(encoded, testEgtsPkgSrRespBytes);
    }

    @Test
    public void testDecode() throws Exception {
        EgtsPackage egtsPackage = new EgtsPackage();
        egtsPackage.decode(testEgtsPkgSrRespBytes);

        EgtsPackage expected = getExpected();

        assertEquals(egtsPackage, expected);
    }
}
