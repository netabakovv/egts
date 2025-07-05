package libs;

import org.example.libs.RecordDataSet;
import org.example.libs.SrStateData;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class SrStateDataTest {

    byte[] testSrStateDataBytes = new byte[]{0x02, 0x7F, 0x00, 0x29, 0x04};

    private SrStateData getExpected() {
        SrStateData srStateData = new SrStateData();
        srStateData.setState((byte) 2);
        srStateData.setMainPowerSourceVoltage((byte) 127);
        srStateData.setBackUpBatteryVoltage((byte) 0);
        srStateData.setInternalBatteryVoltage((byte) 41);
        srStateData.setNMS("1");
        srStateData.setIBU("0");
        srStateData.setBBU("0");

        return srStateData;
    }

    @Test
    public void testEncode() throws IOException {
        SrStateData srStateData = getExpected();
        byte[] encoded = srStateData.encode();

        assertArrayEquals(encoded, testSrStateDataBytes);
    }

    @Test
    public void testDecode() throws IOException {
        SrStateData srStateData = new SrStateData();
        srStateData.decode(testSrStateDataBytes);

        SrStateData expected = getExpected();

        assertEquals(srStateData, expected);
    }

    // проверяем что рекордсет работает правильно с данным типом подзаписи
    @Test
    public void recordSetStateDataTest() throws IOException {
        // Создаем байты RecordDataSet: [SubrecordType, SubrecordLength, ...srStateDataBytes]
        byte SUBRECORD_TYPE_SRSTATE = 0x14; // предполагаемый тип 0x14 (20 в десятичной системе)
        byte SUBRECORD_LENGTH = 5;

        byte[] stateDataRDBytes = new byte[3 + testSrStateDataBytes.length];
        stateDataRDBytes[0] = SUBRECORD_TYPE_SRSTATE;
        stateDataRDBytes[1] = SUBRECORD_LENGTH;
        stateDataRDBytes[2] = 0x00;
        System.arraycopy(testSrStateDataBytes, 0, stateDataRDBytes, 3, testSrStateDataBytes.length);

        // Создаем RecordData с подзаписью SrStateData
        SrStateData srStateData = getExpected();

        RecordDataSet.RecordData rd = new RecordDataSet.RecordData();
        rd.setSubrecordType(SUBRECORD_TYPE_SRSTATE);
        rd.setSubrecordLength(SUBRECORD_LENGTH);
        rd.setSubrecordData(srStateData);

        RecordDataSet expectedRecordDataSet = new RecordDataSet();
        expectedRecordDataSet.addRecord(rd);

        // Кодируем RecordDataSet в байты
        byte[] encoded = expectedRecordDataSet.encode();
        assertArrayEquals(stateDataRDBytes, encoded);

        // Декодируем обратно
        RecordDataSet decodedRecordDataSet = new RecordDataSet();
        decodedRecordDataSet.decode(stateDataRDBytes);

        assertEquals(expectedRecordDataSet, decodedRecordDataSet);
    }
}
