package libs;

import org.example.libs.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.example.libs.RecordDataSet.*;
import static org.junit.jupiter.api.Assertions.*;

public class SrTermIdentityTest {
    byte[] testEgtsSrTermIdentityBin = new byte[]{(byte) 0xB0, 0x09, 0x02, 0x00, 0x10};
    byte[] testEgtsSrTermIdentityPkgBin = new byte[]{0x01, 0x00, 0x03, 0x0B, 0x00, 0x13, 0x00, (byte) 0x86, 0x00, 0x01, (byte) 0xB6, 0x08, 0x00,
            0x5F, 0x00, (byte) 0x99, 0x02, 0x00, 0x00, 0x00, 0x01, 0x01, 0x01, 0x05, 0x00, (byte) 0xB0, 0x09, 0x02, 0x00, 0x10, 0x0D, (byte) 0xCE
    };

    private SrTermIdentity getExpectedTermIdentity() {
        SrTermIdentity srTermIdentity = new SrTermIdentity();
        srTermIdentity.setTerminalIdentifier(133552);
        srTermIdentity.setMNE("0");
        srTermIdentity.setBSE("0");
        srTermIdentity.setNIDE("0");
        srTermIdentity.setSSRA("1");
        srTermIdentity.setLNGCE("0");
        srTermIdentity.setIMSIE("0");
        srTermIdentity.setIMEIE("0");
        srTermIdentity.setHDIDE("0");

        return srTermIdentity;
    }

    private EgtsPackage getExpectedEgtsPackage() {
        EgtsPackage pkg = new EgtsPackage();
        pkg.setProtocolVersion((byte) 1);
        pkg.setSecurityKeyId((byte) 0);
        pkg.setPrefix("00");
        pkg.setRoute("0");
        pkg.setEncryptionAlg("00");
        pkg.setCompression("0");
        pkg.setPriority("11");
        pkg.setHeaderLength((byte) 11);
        pkg.setHeaderEncoding((byte) 0);
        pkg.setFrameDataLength((short) 19);
        pkg.setPacketIdentifier((short) 134);
        pkg.setPacketType(EgtsPacketType.PT_APP_DATA);
        pkg.setHeaderCheckSum((byte) 182);

        ServiceDataSet sds = new ServiceDataSet();
        ServiceDataRecord sdr = new ServiceDataRecord();
        sdr.setRecordLength((short) 8);
        sdr.setRecordNumber((short) 95);
        sdr.setSourceServiceOnDevice("1");
        sdr.setRecipientServiceOnDevice("0");
        sdr.setGroup("0");
        sdr.setRecordProcessingPriority("11");
        sdr.setTimeFieldExists("0");
        sdr.setEventIDFieldExists("0");
        sdr.setObjectIDFieldExists("1");
        sdr.setObjectIdentifier(2);
        sdr.setSourceServiceType(AuthService);
        sdr.setRecipientServiceType(AuthService);

        RecordDataSet rds = new RecordDataSet();
        RecordDataSet.RecordData recordData = new RecordDataSet.RecordData();
        recordData.setSubrecordType(SrTermIdentityType);
        recordData.setSubrecordLength((short) 5);
        recordData.setSubrecordData(getExpectedTermIdentity());

        rds.addRecord(recordData);
        sdr.setRecordDataSet(rds);
        sds.addRecord(sdr);
        pkg.setServicesFrameData(sds);
        pkg.setServicesFrameDataCheckSum((short) 52749);

        return pkg;
    }

    @Test
    public void termIdentityEncodeTest() throws IOException {
        SrTermIdentity srTermIdentity = getExpectedTermIdentity();
        byte[] encoded = srTermIdentity.encode();

        assertArrayEquals(encoded, testEgtsSrTermIdentityBin);
    }

    @Test
    public void termIdentityDecodeTest() throws IOException {
        SrTermIdentity srTermIdentity = new SrTermIdentity();
        srTermIdentity.decode(testEgtsSrTermIdentityBin);

        SrTermIdentity expected = getExpectedTermIdentity();

        assertEquals(srTermIdentity, expected);
    }

    @Test
    public void termIdentityPkgEncodeTest() throws IOException {
        EgtsPackage egtsPackage = getExpectedEgtsPackage();
        byte[] encoded = egtsPackage.encode();

        assertArrayEquals(encoded, testEgtsSrTermIdentityPkgBin);
    }

    @Test
    public void termIdentityPkgDecodeTest() throws IOException {
        EgtsPackage egtsPackage = new EgtsPackage();
        egtsPackage.decode(testEgtsSrTermIdentityPkgBin);

        EgtsPackage expected = getExpectedEgtsPackage();

        assertEquals(egtsPackage, expected);
    }
}
