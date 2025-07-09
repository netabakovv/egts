package org.example.cli.receiver.server;

import com.clickhouse.client.internal.google.protobuf.InvalidProtocolBufferException;
import org.example.libs.*;
import org.example.cli.receiver.storage.NavRecord;
import org.example.cli.receiver.storage.AnSensor;
import org.example.cli.receiver.storage.LiquidSensor;
import org.example.cli.receiver.storage.Repository;

import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionHandler.class);
    private static final byte PROTOCOL_VERSION = 0x01;
    private static final short AUTH_SERVICE = 1;
    private static final short TELEDATA_SERVICE = 2;

    private final Socket socket;
    private final Repository storage;

    public ConnectionHandler(Socket socket, Repository storage) {
        this.socket = socket;
        this.storage = storage;
    }

    @Override
    public void run() {
        try (InputStream in = socket.getInputStream();
             OutputStream out = socket.getOutputStream()) {

            logger.info("New connection from: {}", socket.getRemoteSocketAddress());

            while (!socket.isClosed()) {
                EgtsPackage pkg = readPackage(in);
                if (pkg == null) break;

                long recvTs = Instant.now().getEpochSecond();
                processPackage(pkg, recvTs, out);
            }
        } catch (Exception e) {
            logger.error("Connection handler error", e);
        } finally {
            closeConnection();
        }
    }

    // TODO EGOR EPTA NAHUI SUDAAAAAAAAA
    // TODO EGOR EPTA NAHUI SUDAAAAAAAAA
    private EgtsPackage readPackage(InputStream in) throws IOException {
        byte[] header = in.readNBytes(11);
        if (header.length < 11) {
            logger.warn("Недостаточно данных для заголовка");
            return null;
        }

        if ((header[0] & 0xFF) != 0x01) {
            logger.warn("Неверная версия протокола");
            return null;
        }

        byte headerLength = header[6];
        short frameDataLength = ByteBuffer.wrap(header, 8, 2)
                .order(ByteOrder.LITTLE_ENDIAN)
                .getShort();

        int totalLength = (headerLength & 0xFF) + (frameDataLength & 0xFFFF) + 2;
        if (totalLength < 13 || totalLength > 65535) {
            logger.warn("Неверный размер пакета: {}", totalLength);
            return null;
        }

        byte[] rest = in.readNBytes(totalLength - 11);
        if (rest.length < totalLength - 11) {
            logger.warn("Недостаточно данных для пакета");
            return null;
        }
        // TODO EGOR EPTA NAHUI SUDAAAAAAAAA
        // TODO EGOR EPTA NAHUI SUDAAAAAAAAA
        byte[] fullPacket = new byte[totalLength];
        System.arraycopy(header, 0, fullPacket, 0, 11);
        System.arraycopy(rest, 0, fullPacket, 11, rest.length);

        EgtsPackage pkg = new EgtsPackage();
        EgtsPackage.DecodeResult result = pkg.decode(fullPacket);
        if (result.code() != EgtsPackage.DecodeResultCode.OK) {
            logger.warn("Ошибка при декодировании пакета: {}", result.message());
            return null;
        }

        return result.pkg();
    }




    private void processPackage(EgtsPackage pkg, long recvTs, OutputStream out) throws IOException {
        switch (pkg.getPacketType()) {
            case PT_APP_DATA -> processAppData(pkg, recvTs, out);
            case PT_AUTH -> processAuth(pkg, out);
            default -> logger.warn("Unsupported packet type: {}", pkg.getPacketType());
        }
    }

    private void processAppData(EgtsPackage pkg, long recvTs, OutputStream out) throws IOException {
        ServiceDataSet sds = (ServiceDataSet) pkg.getServicesFrameData();
        List<RecordDataSet.RecordData> responseRecords = new ArrayList<>();
        byte serviceType = 0;
        long clientId = 0;

        for (ServiceDataRecord rec : sds.getRecords()) {
            responseRecords.add(createRecordResponse(rec.getRecordNumber()));
            serviceType = rec.getSourceServiceType();

            if (Boolean.parseBoolean(rec.getObjectIDFieldExists())) {
                clientId = rec.getObjectIdentifier();
            }

            NavRecord navRecord = processServiceRecord(rec, clientId, pkg.getPacketIdentifier(), recvTs);
//            if (navRecord != null) {
//                storage.save(navRecord);
//            }
        }

        byte[] response = createPtResponse(pkg.getPacketIdentifier(), (byte) 0, serviceType, responseRecords);
        out.write(response);
    }

    private NavRecord processServiceRecord(ServiceDataRecord rec, long clientId, short packetId, long recvTs) {
        List<AnSensor> analogSensors = new ArrayList<>();
        List<LiquidSensor> liquidSensors = new ArrayList<>();

        int pdop = 0, hdop = 0, vdop = 0, nsat = 0, ns = 0;
        double lat = 0, lon = 0;
        int speed = 0, course = 0;
        long navTime = 0;

        for (var sub : rec.getRecordDataSet().getRecords()) {
            switch (sub.getSubrecordType()) {
                case RecordDataSet.SrTermIdentityType -> {
                    SrTermIdentity ti = (SrTermIdentity) sub.getSubrecordData();
                    clientId = ti.getTerminalIdentifier();
                }
                case RecordDataSet.SrPosDataType -> {
                    SrPosData d = (SrPosData) sub.getSubrecordData();
                    lat = d.getLatitude(); lon = d.getLongitude();
                    navTime = d.getNavigationTime().toEpochSecond();
                    speed = d.getSpeed(); course = d.getDirection();
                }
                case RecordDataSet.SrExtPosDataType -> {
                    SrExtPosData d = (SrExtPosData) sub.getSubrecordData();
                    pdop = d.getPdop(); hdop = d.getHdop(); vdop = d.getVdop();
                    nsat = d.getSatellites(); ns = d.getNavigationSystem();
                }
                case RecordDataSet.SrAdSensorsDataType -> {
                    SrAdSensorsData d = (SrAdSensorsData) sub.getSubrecordData();
                    d.getActiveAnalogSensors().forEach(s -> analogSensors.add(new AnSensor(s.getSensorNumber(), s.getValue())));
                }
                case RecordDataSet.SrCountersDataType -> {
                    SrCountersData d = (SrCountersData) sub.getSubrecordData();
                    // Обработать счётчики, при необходимости сохранить
                }
                case RecordDataSet.SrStateDataType -> {
                    SrStateData d = (SrStateData) sub.getSubrecordData();
                    // Обработать состояние терминала
                }
                case RecordDataSet.SrAbsAnSensDataType -> {
                    SrAbsSensData d = (SrAbsSensData) sub.getSubrecordData();
                    // Обработать абсолютные аналоговые датчики
                }
                case RecordDataSet.SrPassengersCountersType -> {
                    SrPassengersCountersData d = (SrPassengersCountersData) sub.getSubrecordData();
                    // Обработать пассажирские счётчики
                }
                case RecordDataSet.SrAbsCntrDataType -> {
                    SrAbsCntrData d = (SrAbsCntrData) sub.getSubrecordData();
                    analogSensors.add(new AnSensor(d.getCounterNumber(),d.getCounterValue()));
                }
                case RecordDataSet.SrLiquidLevelSensorType -> {
                    SrLiquidLevelSensor d = (SrLiquidLevelSensor) sub.getSubrecordData();
                    liquidSensors.add(LiquidSensor.of(
                            d.getLiquidLevelSensorNumber(),
                            d.getLiquidLevelSensorErrorFlag(),
                            d.getLiquidLevelSensorData(),
                            d.getLiquidLevelSensorData()
                    ));
                }
            }
        }

        if (navTime > 0) {
            NavRecord nav = NavRecord.of(
                    clientId, packetId, navTime, recvTs,
                    lat, lon, speed, pdop, hdop, vdop, nsat, ns,
                    course,
                    analogSensors.isEmpty() ? null : analogSensors,
                    liquidSensors.isEmpty() ? null : liquidSensors
            );
            return nav;
        }
        return null;
    }

    private void processAuth(EgtsPackage pkg, OutputStream out) throws IOException {
        byte[] response = createSrResultCode(pkg.getPacketIdentifier(), (byte) 0);
        out.write(response);
    }

    private RecordDataSet.RecordData createRecordResponse(short recordNumber) {
        RecordDataSet.RecordData rd = new RecordDataSet.RecordData();
        rd.setSubrecordType(RecordDataSet.SrRecordResponseType);
        rd.setSubrecordLength((short) 3);

        SrResponse sr = new SrResponse();
        sr.setConfirmedRecordNumber(recordNumber);
        sr.setRecordStatus((byte) 0);

        rd.setSubrecordData(sr);
        return rd;
    }

    private byte[] createPtResponse(short pid, byte resultCode, byte serviceType,
                                    List<RecordDataSet.RecordData> records) throws IOException {
        PtResponse pr = new PtResponse();
        pr.setResponsePacketID(pid);
        pr.setProcessingResult(resultCode);

        if (records != null && !records.isEmpty()) {
            RecordDataSet rds = new RecordDataSet();
            records.forEach(rds::addRecord);

            ServiceDataRecord sdr = new ServiceDataRecord();
            sdr.setRecordNumber((short) 1);
            sdr.setSourceServiceType(serviceType);
            sdr.setRecipientServiceType(serviceType);
            sdr.setRecordDataSet(rds);

            ServiceDataSet sds = new ServiceDataSet();
            sds.addRecord(sdr);
            pr.setSdr(sds);
        }

        EgtsPackage pkg = new EgtsPackage();
        pkg.setProtocolVersion(PROTOCOL_VERSION);
        pkg.setSecurityKeyId((byte) 0);
        pkg.setPrefix("00");
        pkg.setRoute("0");
        pkg.setEncryptionAlg("00");
        pkg.setCompression("0");
        pkg.setPriority("00");
        pkg.setHeaderLength((byte) 11);
        pkg.setHeaderEncoding((byte) 0);
        pkg.setPacketIdentifier((short) (pid + 1));
        pkg.setPacketType(EgtsPacketType.PT_RESPONSE);
        pkg.setServicesFrameData(pr);

        return pkg.encode();
    }

    private byte[] createSrResultCode(short pid, byte code) throws IOException {
        SrResultCode rc = new SrResultCode();
        rc.setResultCode(code);

        RecordDataSet.RecordData rd = new RecordDataSet.RecordData();
        rd.setSubrecordType(RecordDataSet.SrResultCodeType);
        rd.setSubrecordLength((short) 1);
        rd.setSubrecordData(rc);

        RecordDataSet rds = new RecordDataSet();
        rds.addRecord(rd);

        ServiceDataRecord sdr = new ServiceDataRecord();
        sdr.setRecordNumber((short) 1);
        sdr.setSourceServiceType((byte) AUTH_SERVICE);
        sdr.setRecipientServiceType((byte) AUTH_SERVICE);
        sdr.setRecordDataSet(rds);

        ServiceDataSet sds = new ServiceDataSet();
        sds.addRecord(sdr);

        EgtsPackage pkg = new EgtsPackage();
        pkg.setProtocolVersion(PROTOCOL_VERSION);
        pkg.setSecurityKeyId((byte) 0);
        pkg.setPrefix("00");
        pkg.setRoute("0");
        pkg.setEncryptionAlg("00");
        pkg.setCompression("0");
        pkg.setPriority("00");
        pkg.setHeaderLength((byte) 11);
        pkg.setHeaderEncoding((byte) 0);
        pkg.setPacketIdentifier((short) (pid + 1));
        pkg.setPacketType(EgtsPacketType.PT_RESPONSE);
        PtResponse pr = new PtResponse();
        pr.setResponsePacketID(pid);
        pr.setProcessingResult((byte)0);
        pr.setSdr(sdr);
        pkg.setServicesFrameData(pr);

        return pkg.encode();
    }

    private void closeConnection() {
        try {
            if (!socket.isClosed()) {
                socket.close();
                logger.info("Connection closed: {}", socket.getRemoteSocketAddress());
            }
        } catch (IOException e) {
            logger.error("Error closing socket", e);
        }
    }
}