package libs;

import org.example.libs.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.postgresql.core.EncodingPredictor;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.Assert.*;

public class EgtsPackageTest {
    private static final byte[] EGTS_PKG_POS_DATA_BYTES = {
            0x01, 0x00, 0x03, 0x0B, 0x00, 0x23, 0x00, (byte) 0x8A,
            0x00, 0x01, 0x49, 0x18, 0x00, 0x61, 0x00, (byte) 0x99,
            (byte) 0xB0, 0x09, 0x02, 0x00, 0x02, 0x02, 0x10, 0x15,
            0x00, (byte) 0xD5, 0x3F, 0x01, 0x10, 0x6F, 0x1C, 0x05,
            (byte) 0x9E, 0x7A, (byte) 0xB5, 0x3C, 0x35, 0x01, (byte) 0xD0, (byte) 0x87,
            0x2C, 0x01, 0x00, 0x00, 0x00, 0x00, (byte) 0xCC, 0x27
    };

    @Test
    void test_encode_pos_data() throws IOException {
        SrPosData srPosData = new SrPosData();
        srPosData.setNavigationTime(LocalDateTime.of(2018, 7, 5, 20, 8, 53, 0).atZone(ZoneOffset.UTC).toInstant());
        srPosData.setLatitude(55.55389399769574);
        srPosData.setLongitude(37.43236696287812);
        srPosData.setAlte("0");
        srPosData.setLohs("0");
        srPosData.setLahs("0");
        srPosData.setMv("0");
        srPosData.setBb("0");
        srPosData.setCs("0");
        srPosData.setFix("0");
        srPosData.setVld("1");
        srPosData.setDirectionHighestBit((byte) 1);
        srPosData.setAltitudeSign(0);
        srPosData.setSpeed(200);
        srPosData.setOdometer(1);
        srPosData.setDirection((byte) 172);
        srPosData.setDigitalInputs((byte) 0);
        srPosData.setSource((byte) 0);

        RecordDataSet.RecordData recordData = new RecordDataSet.RecordData();
        recordData.setSubrecordType(RecordDataSet.SrPosDataType);
        recordData.setSubrecordLength((short) srPosData.length());
        recordData.setSubrecordData(srPosData);

        ServiceDataRecord serviceDataRecord = new ServiceDataRecord();
        serviceDataRecord.setRecordNumber((short) 97);
        serviceDataRecord.setSourceServiceOnDevice("1");
        serviceDataRecord.setRecipientServiceOnDevice("0");
        serviceDataRecord.setGroup("0");
        serviceDataRecord.setRecordProcessingPriority("11");
        serviceDataRecord.setObjectIDFieldExists("1");
        serviceDataRecord.setObjectIdentifier(133552);
        serviceDataRecord.setSourceServiceType((byte) 2);
        serviceDataRecord.setRecipientServiceType((byte) 2);

        // Добавляем запись в RecordDataSet
        serviceDataRecord.setRecordDataSet(new RecordDataSet());
        serviceDataRecord.getRecordDataSet().addRecord(recordData); // ← Теперь работает!

        ServiceDataSet servicesFrameData = new ServiceDataSet();
        servicesFrameData.addRecord(serviceDataRecord);

        EgtsPackage egtsPkg = new EgtsPackage();
        egtsPkg.setProtocolVersion((byte) 1);
        egtsPkg.setSecurityKeyId((byte) 0);
        egtsPkg.setPrefix("00");
        egtsPkg.setRoute("0");
        egtsPkg.setEncryptionAlg("00");
        egtsPkg.setCompression("0");
        egtsPkg.setPriority("11");
        egtsPkg.setHeaderEncoding((byte) 0);
        egtsPkg.setPacketIdentifier((short) 138);
        egtsPkg.setPacketType(EgtsPacketType.PT_APP_DATA);
        egtsPkg.setServicesFrameData(servicesFrameData);

        byte[] encoded = egtsPkg.encode();

        assertArrayEquals(EGTS_PKG_POS_DATA_BYTES, encoded);
    }

    @Test
    void test_decode_pos_data() {
        EgtsPackage decoded = new EgtsPackage();
        EgtsPackage.DecodeResult result = decoded.decode(EGTS_PKG_POS_DATA_BYTES);
        assertEquals(EgtsPackage.DecodeResultCode.OK, result.code());

        EgtsPackage pkg = result.pkg();

        assertEquals(1, pkg.getProtocolVersion());
        assertEquals(0, pkg.getSecurityKeyId());
        assertEquals("00", pkg.getPrefix());
        assertEquals("0", pkg.getRoute());
        assertEquals("00", pkg.getEncryptionAlg());
        assertEquals("0", pkg.getCompression());
        assertEquals("11", pkg.getPriority());
        assertEquals(0, pkg.getHeaderEncoding());
        assertEquals(35, pkg.getFrameDataLength());
        assertEquals(138, pkg.getPacketIdentifier());
        assertEquals(EgtsPacketType.PT_APP_DATA, pkg.getPacketType());

        assertNotNull(pkg.getServicesFrameData());
        ServiceDataSet sfd = (ServiceDataSet) pkg.getServicesFrameData();
        List<ServiceDataRecord> records = sfd.getRecords();
        assertEquals(1, records.size());

        ServiceDataRecord rec = records.get(0);
        assertEquals(97, rec.getRecordNumber());
        assertEquals("1", rec.getSourceServiceOnDevice());
        assertEquals("0", rec.getRecipientServiceOnDevice());
        assertEquals("0", rec.getGroup());
        assertEquals("11", rec.getRecordProcessingPriority());
        assertEquals("1", rec.getObjectIDFieldExists());
        assertEquals(133552, rec.getObjectIdentifier());
        assertEquals(2, rec.getSourceServiceType());
        assertEquals(2, rec.getRecipientServiceType());

        List<RecordDataSet.RecordData> rds = rec.getRecordDataSet().getRecords();
        assertEquals(1, rds.size());

        RecordDataSet.RecordData rd = rds.get(0);
        assertEquals(RecordDataSet.SrPosDataType, rd.getSubrecordType());
        assertEquals(21, rd.getSubrecordLength());

        SrPosData posData = (SrPosData) rd.getSubrecordData();
        assertEquals(LocalDateTime.of(2018, 7, 5, 20, 8, 53, 0).atZone(ZoneOffset.UTC).toInstant(), posData.getNavigationTime());
        assertEquals(55.55389399769574, posData.getLatitude(), 1e-10);
        assertEquals(37.43236696287812, posData.getLongitude(), 1e-10);
        assertEquals("0", posData.getAlte());
        assertEquals("0", posData.getLohs());
        assertEquals("0", posData.getLahs());
        assertEquals("0", posData.getMv());
        assertEquals("0", posData.getBb());
        assertEquals("0", posData.getCs());
        assertEquals("0", posData.getFix());
        assertEquals("1", posData.getVld());
        assertEquals(1, posData.getDirectionHighestBit());
        assertEquals(0, posData.getAltitudeSign());
        assertEquals(200, posData.getSpeed());
        assertEquals((byte) 172, posData.getDirection());
        assertEquals(1, posData.getOdometer());
        assertEquals(0, posData.getDigitalInputs());
        assertEquals(0, posData.getSource());
    }
}
