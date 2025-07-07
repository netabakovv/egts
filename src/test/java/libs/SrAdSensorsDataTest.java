package libs;

import org.example.libs.SrAdSensorsData;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class SrAdSensorsDataTest {
    private final byte[] srAdSensorsDataBytes = new byte[]{
            0x00, 0x0F, (byte) 0xFF,
            0x00, 0x00, 0x00,
            0x00, 0x00, 0x00,
            0x00, 0x00, 0x00,
            0x00, 0x00, 0x00,
            0x00, 0x00, 0x00,
            0x00, 0x00, 0x00,
            0x00, 0x00, 0x00,
            0x00, 0x00, 0x00
    };

    private final SrAdSensorsData testEgtsSrAdSensorsData = new SrAdSensorsData();

    {
        // Инициализация тестовых данных через сеттеры
        testEgtsSrAdSensorsData.setDigitalInputsOctetExists1("0");
        testEgtsSrAdSensorsData.setDigitalInputsOctetExists2("0");
        testEgtsSrAdSensorsData.setDigitalInputsOctetExists3("0");
        testEgtsSrAdSensorsData.setDigitalInputsOctetExists4("0");
        testEgtsSrAdSensorsData.setDigitalInputsOctetExists5("0");
        testEgtsSrAdSensorsData.setDigitalInputsOctetExists6("0");
        testEgtsSrAdSensorsData.setDigitalInputsOctetExists7("0");
        testEgtsSrAdSensorsData.setDigitalInputsOctetExists8("0");

        testEgtsSrAdSensorsData.setDigitalOutputs((byte) 15);

        testEgtsSrAdSensorsData.setAnalogSensorFieldExists1("1");
        testEgtsSrAdSensorsData.setAnalogSensorFieldExists2("1");
        testEgtsSrAdSensorsData.setAnalogSensorFieldExists3("1");
        testEgtsSrAdSensorsData.setAnalogSensorFieldExists4("1");
        testEgtsSrAdSensorsData.setAnalogSensorFieldExists5("1");
        testEgtsSrAdSensorsData.setAnalogSensorFieldExists6("1");
        testEgtsSrAdSensorsData.setAnalogSensorFieldExists7("1");
        testEgtsSrAdSensorsData.setAnalogSensorFieldExists8("1");

        testEgtsSrAdSensorsData.setAdditionalDigitalInputsOctet1((byte) 0);

        testEgtsSrAdSensorsData.setAnalogSensor1(0);
        testEgtsSrAdSensorsData.setAnalogSensor2(0);
        testEgtsSrAdSensorsData.setAnalogSensor3(0);
        testEgtsSrAdSensorsData.setAnalogSensor4(0);
        testEgtsSrAdSensorsData.setAnalogSensor5(0);
        testEgtsSrAdSensorsData.setAnalogSensor6(0);
        testEgtsSrAdSensorsData.setAnalogSensor7(0);
        testEgtsSrAdSensorsData.setAnalogSensor8(0);
    }

    @Test
    public void testEncode() throws IOException {
        byte[] encoded = testEgtsSrAdSensorsData.encode();
        assertArrayEquals(srAdSensorsDataBytes, encoded);
    }

    @Test
    public void testDecode() throws IOException {
        SrAdSensorsData decoded = new SrAdSensorsData();
        decoded.decode(srAdSensorsDataBytes);

        assertEquals(testEgtsSrAdSensorsData.getDigitalInputsOctetExists1(), decoded.getDigitalInputsOctetExists1());
        assertEquals(testEgtsSrAdSensorsData.getDigitalInputsOctetExists2(), decoded.getDigitalInputsOctetExists2());
        assertEquals(testEgtsSrAdSensorsData.getDigitalInputsOctetExists3(), decoded.getDigitalInputsOctetExists3());
        assertEquals(testEgtsSrAdSensorsData.getDigitalInputsOctetExists4(), decoded.getDigitalInputsOctetExists4());
        assertEquals(testEgtsSrAdSensorsData.getDigitalInputsOctetExists5(), decoded.getDigitalInputsOctetExists5());
        assertEquals(testEgtsSrAdSensorsData.getDigitalInputsOctetExists6(), decoded.getDigitalInputsOctetExists6());
        assertEquals(testEgtsSrAdSensorsData.getDigitalInputsOctetExists7(), decoded.getDigitalInputsOctetExists7());
        assertEquals(testEgtsSrAdSensorsData.getDigitalInputsOctetExists8(), decoded.getDigitalInputsOctetExists8());

        assertEquals(testEgtsSrAdSensorsData.getDigitalOutputs(), decoded.getDigitalOutputs());

        assertEquals(testEgtsSrAdSensorsData.getAnalogSensorFieldExists1(), decoded.getAnalogSensorFieldExists1());
        assertEquals(testEgtsSrAdSensorsData.getAnalogSensorFieldExists2(), decoded.getAnalogSensorFieldExists2());
        assertEquals(testEgtsSrAdSensorsData.getAnalogSensorFieldExists3(), decoded.getAnalogSensorFieldExists3());
        assertEquals(testEgtsSrAdSensorsData.getAnalogSensorFieldExists4(), decoded.getAnalogSensorFieldExists4());
        assertEquals(testEgtsSrAdSensorsData.getAnalogSensorFieldExists5(), decoded.getAnalogSensorFieldExists5());
        assertEquals(testEgtsSrAdSensorsData.getAnalogSensorFieldExists6(), decoded.getAnalogSensorFieldExists6());
        assertEquals(testEgtsSrAdSensorsData.getAnalogSensorFieldExists7(), decoded.getAnalogSensorFieldExists7());
        assertEquals(testEgtsSrAdSensorsData.getAnalogSensorFieldExists8(), decoded.getAnalogSensorFieldExists8());

        assertEquals(testEgtsSrAdSensorsData.getAdditionalDigitalInputsOctet1(), decoded.getAdditionalDigitalInputsOctet1());

        assertEquals(testEgtsSrAdSensorsData.getAnalogSensor1(), decoded.getAnalogSensor1());
        assertEquals(testEgtsSrAdSensorsData.getAnalogSensor2(), decoded.getAnalogSensor2());
        assertEquals(testEgtsSrAdSensorsData.getAnalogSensor3(), decoded.getAnalogSensor3());
        assertEquals(testEgtsSrAdSensorsData.getAnalogSensor4(), decoded.getAnalogSensor4());
        assertEquals(testEgtsSrAdSensorsData.getAnalogSensor5(), decoded.getAnalogSensor5());
        assertEquals(testEgtsSrAdSensorsData.getAnalogSensor6(), decoded.getAnalogSensor6());
        assertEquals(testEgtsSrAdSensorsData.getAnalogSensor7(), decoded.getAnalogSensor7());
        assertEquals(testEgtsSrAdSensorsData.getAnalogSensor8(), decoded.getAnalogSensor8());
    }
}
