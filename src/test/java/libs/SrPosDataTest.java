package libs;

import org.example.libs.SrPosData;
import org.junit.jupiter.api.Test;

import java.time.*;
import static org.junit.jupiter.api.Assertions.*;

public class SrPosDataTest {
    byte[] testEgtsSrPosDataBytes = new byte[]{0x55, (byte) 0x91, 0x02, 0x10, 0x6F, 0x1C, 0x05, (byte) 0x9E, 0x7A, (byte) 0xB5, 0x3C, 0x35,
            0x01, (byte) 0xD0, (byte) 0x87, 0x2C, 0x01, 0x00, 0x00, 0x00, 0x00};

    private SrPosData getExpected() {
        SrPosData res = new SrPosData();
        res.setNavigationTime(Instant.parse("2018-07-06T20:08:53Z"));
        res.setLatitude(55.55389399769574);
        res.setLongitude(37.43236696287812);
        res.setAlte("0");
        res.setLohs("0");
        res.setLahs("0");
        res.setMv("0");
        res.setBb("0");
        res.setCs("0");
        res.setFix("0");
        res.setVld("1");
        res.setDirectionHighestBit((byte) 1);
        res.setAltitudeSign(0);
        res.setSpeed(200);
        res.setOdometer(1);
        res.setDirection((byte) 172);
        res.setDigitalInputs((byte) 0);
        res.setSource((byte) 0);

        return res;
    }

    @Test
    public void testEncode() throws Exception{
        SrPosData expected = getExpected();
        byte[] encoded = expected.encode();

        assertArrayEquals(encoded, testEgtsSrPosDataBytes);
    }

    @Test
    public void testDecode() throws Exception {
        SrPosData data = new SrPosData();
        data.decode(testEgtsSrPosDataBytes);

        SrPosData expected = getExpected();

        assertEquals(expected, data);
    }

}