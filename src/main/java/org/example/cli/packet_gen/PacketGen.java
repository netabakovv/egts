package org.example.cli.packet_gen;

import org.example.libs.*;
import picocli.CommandLine;
import picocli.CommandLine.*;

import java.io.IOException;
import java.net.Socket;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.concurrent.Callable;

@Command(name = "packetgen", mixinStandardHelpOptions = true, version = "packetgen 1.0",
        description = "Generates and sends an EGTS packet.")
public class PacketGen implements Callable<Integer> {

    @Option(names = "--pid", required = true, description = "Packet ID (required)")
    private int pid;

    @Option(names = "--oid", required = true, description = "Object ID (required)")
    private int oid;

    @Option(names = "--time", required = true, description = "Timestamp in RFC3339 format (required)")
    private String timestamp;

    @Option(names = "--liquid", description = "Liquid level value")
    private int liquidLevel = 0;

    @Option(names = "--lat", description = "Latitude")
    private double latitude = 0.0;

    @Option(names = "--lon", description = "Longitude")
    private double longitude = 0.0;

    @Option(names = "--server", description = "Server address, format host:port")
    private String server = "localhost:5555";

    @Option(names = "--timeout", description = "Timeout in seconds")
    private int timeout = 0;

    @Override
    public Integer call() {
        Instant time;
        try {
            time = OffsetDateTime.parse(timestamp, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toInstant();
        } catch (Exception e) {
            System.err.println("Invalid time format (RFC3339 expected): " + timestamp);
            return 1;
        }

        try {
            EgtsPackage packet = new EgtsPackage();
            packet.setPacketIdentifier((short) pid);
            packet.setProtocolVersion((byte) 1);
            packet.setPrefix("00");
            packet.setRoute("0");
            packet.setEncryptionAlg("00");
            packet.setCompression("0");
            packet.setPriority("10");
            packet.setHeaderLength((byte) 11);
            packet.setHeaderEncoding((byte) 0);
            packet.setPacketType(EgtsPacketType.PT_APP_DATA);

            ServiceDataSet sds = new ServiceDataSet();
            ServiceDataRecord sdr = new ServiceDataRecord();

            sdr.setRecordNumber((short) 1);
            sdr.setRecipientServiceOnDevice("0");
            sdr.setGroup("0");
            sdr.setRecordProcessingPriority("10");
            sdr.setTimeFieldExists("0");
            sdr.setEventIDFieldExists("1");
            sdr.setObjectIDFieldExists("1");
            sdr.setSourceServiceType((byte) 2);
            sdr.setRecipientServiceType((byte) 2);
            sdr.setObjectIdentifier(oid);
            sdr.setEventIdentifier(3436);

            RecordDataSet rds = new RecordDataSet();

            // === SrPosData ===
            SrPosData pos = new SrPosData();
            pos.setNavigationTime(time.atZone(ZoneOffset.UTC).toLocalDateTime());
            pos.setLatitude(latitude);
            pos.setLongitude(longitude);
            pos.setALTE(true);
            pos.setLOHS(false);
            pos.setLAHS(false);
            pos.setMV(true);
            pos.setBB(true);
            pos.setCS(false);
            pos.setSpeed(34);
            pos.setDirection((byte) 172);
            pos.setAltitude(30);
            pos.setAltitudeSign(0);
            pos.setOdometer(191);
            pos.setDigitalInputs((byte) 144);
            pos.setSource((byte) 0);
            pos.setVLD(true);
            pos.setFIX(true);

            rds.addRecord(createRecord(RecordDataSet.SrPosDataType, pos));

            // === SrLiquidLevelSensor ===
            SrLiquidLevelSensor liquid = new SrLiquidLevelSensor();
            liquid.setLiquidLevelSensorErrorFlag("1");
            liquid.setLiquidLevelSensorValueUnit("00");
            liquid.setRawDataFlag("0");
            liquid.setLiquidLevelSensorNumber((byte) 1);
            liquid.setModuleAddress(1);
            liquid.setLiquidLevelSensorData(liquidLevel);

            rds.addRecord(createRecord(RecordDataSet.SrLiquidLevelSensorType, liquid));

            sdr.setRecordDataSet(rds);
            sds.addRecord(sdr);

            packet.setServicesFrameData(sds);
            byte[] encoded = packet.encode();

            String[] hostPort = server.split(":");
            String host = hostPort[0];
            int port = Integer.parseInt(hostPort[1]);

            try (Socket socket = new Socket(host, port)) {
                socket.setSoTimeout(timeout * 1000);
                socket.getOutputStream().write(encoded);

                byte[] ackBuf = new byte[1024];
                int read = socket.getInputStream().read(ackBuf);
                if (read <= 0) throw new IOException("No response");

                EgtsPackage ackPacket = new EgtsPackage();
                ackPacket.decode(Arrays.copyOfRange(ackBuf, 0, read));

                if (!(ackPacket.getServicesFrameData() instanceof PtResponse)) {
                    System.err.println("Received packet is not EGTS ACK");
                    return 1;
                }

                PtResponse response = (PtResponse) ackPacket.getServicesFrameData();
                if (response.getResponsePacketID() != pid) {
                    System.err.printf("ACK PID mismatch: %d (expected %d)%n", response.getResponsePacketID(), pid);
                    return 1;
                }

                if (response.getProcessingResult() != 0) {
                    System.err.printf("Processing result != 0: %d%n", response.getProcessingResult());
                    return 1;
                }

                if (response.getSdr() instanceof ServiceDataSet respSds) {
                    for (ServiceDataRecord rec : respSds.getRecords()) {
                        for (RecordDataSet.RecordData rd : rec.getRecordDataSet().getRecords()) {
                            if (rd.getSubrecordType() == RecordDataSet.SrRecordResponseType) {
                                SrResponse sr = (SrResponse) rd.getSubrecordData();
                                if (sr.getRecordStatus() != 0) {
                                    System.err.printf("RecordStatus != 0: %d%n", sr.getRecordStatus());
                                    return 1;
                                }
                            }
                        }
                    }
                }

                System.out.println("Packet sent and processed correctly.");
            }

        } catch (IOException e) {
            System.err.println("IO Error: " + e.getMessage());
            return 1;
        }

        return 0;
    }

    private static RecordDataSet.RecordData createRecord(byte type, BinaryData data) {
        RecordDataSet.RecordData record = new RecordDataSet.RecordData();
        record.setSubrecordType(type);
        record.setSubrecordData(data);
        return record;
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new PacketGen()).execute(args);
        System.exit(exitCode);
    }
}
