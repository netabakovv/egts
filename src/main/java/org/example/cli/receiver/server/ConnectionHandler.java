package org.example.cli.receiver.server;

import org.example.libs.*;
import org.example.cli.receiver.storage.NavRecord;
import org.example.cli.receiver.storage.AnSensor;
import org.example.cli.receiver.storage.LiquidSensor;
import org.example.cli.receiver.storage.Repository;

import java.io.*;
import java.net.Socket;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionHandler.class);
    private final Socket socket;
    private final Repository storage;

    public ConnectionHandler(Socket socket, Repository storage) {
        this.socket = socket;
        this.storage = storage;
    }

    @Override
    public void run() {
        try (InputStream input = socket.getInputStream();
             OutputStream output = socket.getOutputStream()) {

            byte[] header = new byte[10];
            while (true) {
                int bytesRead = input.read(header);
                if (bytesRead == -1) break;

                if (header[0] != 0x01) {
                    logger.warn("Неверный формат заголовка");
                    break;
                }

                int bodyLen = ((header[5] & 0xFF) << 8) | (header[6] & 0xFF);
                int pkgLen = header[3] & 0xFF;
                if (bodyLen > 0) {
                    pkgLen += bodyLen + 2; // CRC
                }

                byte[] body = new byte[pkgLen - 10];
                input.read(body);

                byte[] fullPacket = new byte[header.length + body.length];
                System.arraycopy(header, 0, fullPacket, 0, header.length);
                System.arraycopy(body, 0, fullPacket, header.length, body.length);

                EgtsPackage egtsPkg = new EgtsPackage();
                EgtsPackage.DecodeResult result = egtsPkg.decode(fullPacket);
                if (result.code() != EgtsPackage.DecodeResultCode.OK) {
                    logger.warn("Ошибка декодирования пакета: {}", result.message());
                    continue;
                }

                Instant receivedTime = Instant.now();

                if (egtsPkg.getPacketType() == EgtsPacketType.PT_APP_DATA) {
                    ServiceDataSet sds = (ServiceDataSet) egtsPkg.getServicesFrameData();
                    for (var record : sds.getRecords()) {
                        NavRecord navRecord = new NavRecord(
                                0, 0, 0, 0,
                                0, 0, 0, 0, 0, 0, 0, 0, 0, null, null);
                        boolean save = false;

                        for (var rd : record.getRecordDataSet().getRecords()) {
                            switch (rd.getSubrecordType()) {
                                case RecordDataSet.SrPosDataType -> {
                                    var pos = (SrPosData) rd.getSubrecordData();
                                    navRecord = new NavRecord(
                                            navRecord.client(),
                                            0,
                                            pos.getNavigationTime().getSecond(),
                                            receivedTime.getEpochSecond(),
                                            pos.getLatitude(),
                                            pos.getLongitude(),
                                            pos.getSpeed(),
                                            0,
                                            0,
                                            0,
                                            0,
                                            0,
                                            pos.getDirection(),
                                            new ArrayList<>(),
                                            new ArrayList<>()
                                    );
                                    save = true;
                                }
                                case RecordDataSet.SrTermIdentityType -> {
                                    var term = (SrTermIdentity) rd.getSubrecordData();
                                    navRecord = new NavRecord(
                                            term.getTerminalIdentifier(),
                                            navRecord.packetID(),
                                            navRecord.navigationTimestamp(),
                                            navRecord.receivedTimestamp(),
                                            navRecord.latitude(),
                                            navRecord.longitude(),
                                            navRecord.speed(),
                                            navRecord.pdop(),
                                            navRecord.hdop(),
                                            navRecord.vdop(),
                                            navRecord.nsat(),
                                            navRecord.ns(),
                                            navRecord.course(),
                                            navRecord.anSensors(),
                                            navRecord.liquidSensors()
                                    );
                                }
                                case RecordDataSet.SrAdSensorsDataType -> {
                                    var ad = (SrAdSensorsData) rd.getSubrecordData();
                                    List<AnSensor> sensors = new ArrayList<>();
                                    if ("1".equals(ad.getAnalogSensorFieldExists1()))
                                        sensors.add(AnSensor.of(1, ad.getAnalogSensor1()));
                                    if ("1".equals(ad.getAnalogSensorFieldExists2()))
                                        sensors.add(AnSensor.of(2, ad.getAnalogSensor2()));
                                    if ("1".equals(ad.getAnalogSensorFieldExists3()))
                                        sensors.add(AnSensor.of(3, ad.getAnalogSensor3()));
                                    if ("1".equals(ad.getAnalogSensorFieldExists4()))
                                        sensors.add(AnSensor.of(4, ad.getAnalogSensor4()));
                                    if ("1".equals(ad.getAnalogSensorFieldExists5()))
                                        sensors.add(AnSensor.of(5, ad.getAnalogSensor5()));
                                    if ("1".equals(ad.getAnalogSensorFieldExists6()))
                                        sensors.add(AnSensor.of(6, ad.getAnalogSensor6()));
                                    if ("1".equals(ad.getAnalogSensorFieldExists7()))
                                        sensors.add(AnSensor.of(7, ad.getAnalogSensor7()));
                                    if ("1".equals(ad.getAnalogSensorFieldExists8()))
                                        sensors.add(AnSensor.of(8, ad.getAnalogSensor8()));

                                    navRecord = new NavRecord(
                                            navRecord.client(),
                                            navRecord.packetID(),
                                            navRecord.navigationTimestamp(),
                                            navRecord.receivedTimestamp(),
                                            navRecord.latitude(),
                                            navRecord.longitude(),
                                            navRecord.speed(),
                                            navRecord.pdop(),
                                            navRecord.hdop(),
                                            navRecord.vdop(),
                                            navRecord.nsat(),
                                            navRecord.ns(),
                                            navRecord.course(),
                                            sensors,
                                            navRecord.liquidSensors()
                                    );
                                }
                                case RecordDataSet.SrLiquidLevelSensorType -> {
                                    var fuel = (SrLiquidLevelSensor) rd.getSubrecordData();
                                    List<LiquidSensor> sensors = new ArrayList<>();
                                    sensors.add(LiquidSensor.of(fuel.getLiquidLevelSensorNumber(), fuel.getLiquidLevelSensorErrorFlag(), fuel.getLiquidLevelSensorData(), 0));
                                    navRecord = new NavRecord(
                                            navRecord.client(),
                                            navRecord.packetID(),
                                            navRecord.navigationTimestamp(),
                                            navRecord.receivedTimestamp(),
                                            navRecord.latitude(),
                                            navRecord.longitude(),
                                            navRecord.speed(),
                                            navRecord.pdop(),
                                            navRecord.hdop(),
                                            navRecord.vdop(),
                                            navRecord.nsat(),
                                            navRecord.ns(),
                                            navRecord.course(),
                                            navRecord.anSensors(),
                                            sensors
                                    );
                                }
                                case RecordDataSet.SrExtPosDataType -> { // EGTS_SR_EXT_POS_DATA
                                    var extPos = (SrExtPosData) rd.getSubrecordData();
                                    navRecord = new NavRecord(
                                            navRecord.client(),
                                            navRecord.packetID(),
                                            navRecord.navigationTimestamp(),
                                            navRecord.receivedTimestamp(),
                                            navRecord.latitude(),
                                            navRecord.longitude(),
                                            navRecord.speed(),
                                            extPos.getPdop(),
                                            extPos.getHdop(),
                                            extPos.getVdop(),
                                            extPos.getSatellites(),
                                            extPos.getNavigationSystem(),
                                            navRecord.course(),
                                            navRecord.anSensors(),
                                            navRecord.liquidSensors()
                                    );
                                }
                                case RecordDataSet.SrStateDataType -> { // EGTS_SR_STATE_DATA
                                    var state = (SrStateData) rd.getSubrecordData();
                                    // Обработка состояния устройства
                                    logger.info("Состояние АСН: режим={}, напряжение={}V",
                                            state.getState(), state.getMainPowerSourceVoltage());
                                }
                                case RecordDataSet.SrAbsAnSensDataType -> { // EGTS_SR_ABS_AN_SENS_DATA
                                    var absAn = (SrAbsSensData) rd.getSubrecordData();
                                    List<AnSensor> sensors = new ArrayList<>(navRecord.anSensors());
                                    sensors.add(AnSensor.of(absAn.getSensorNumber(), absAn.getValue()));
                                    navRecord = new NavRecord(
                                            navRecord.client(),
                                            navRecord.packetID(),
                                            navRecord.navigationTimestamp(),
                                            navRecord.receivedTimestamp(),
                                            navRecord.latitude(),
                                            navRecord.longitude(),
                                            navRecord.speed(),
                                            navRecord.pdop(),
                                            navRecord.hdop(),
                                            navRecord.vdop(),
                                            navRecord.nsat(),
                                            navRecord.ns(),
                                            navRecord.course(),
                                            sensors,
                                            navRecord.liquidSensors()
                                    );
                                }
                                case RecordDataSet.SrAbsCntrDataType -> { // EGTS_SR_ABS_AN_SENS_DATA
                                    var absCntr = (SrAbsCntrData) rd.getSubrecordData();
                                    List<AnSensor> sensors = new ArrayList<>(navRecord.anSensors());
                                    sensors.add(AnSensor.of(absCntr.getCounterNumber(), absCntr.getCounterValue()));
                                    navRecord = new NavRecord(
                                            navRecord.client(),
                                            navRecord.packetID(),
                                            navRecord.navigationTimestamp(),
                                            navRecord.receivedTimestamp(),
                                            navRecord.latitude(),
                                            navRecord.longitude(),
                                            navRecord.speed(),
                                            navRecord.pdop(),
                                            navRecord.hdop(),
                                            navRecord.vdop(),
                                            navRecord.nsat(),
                                            navRecord.ns(),
                                            navRecord.course(),
                                            sensors,
                                            navRecord.liquidSensors()
                                    );

                                }
                                default -> logger.info("Неизвестная подзапись: {}", rd.getSubrecordType());
                            }
                        }
//
//                        if (save) {
//                            storage.save(navRecord);
//                        }
                    }

                    PtResponse pt = new PtResponse();
                    pt.setResponsePacketID(egtsPkg.getPacketIdentifier());
                    pt.setProcessingResult((byte) 0);

                    EgtsPackage resp = new EgtsPackage();
                    resp.setProtocolVersion((byte) 1);
                    resp.setSecurityKeyId((byte) 0);
                    resp.setPrefix("00");
                    resp.setRoute("0");
                    resp.setEncryptionAlg("00");
                    resp.setCompression("0");
                    resp.setPriority("00");
                    resp.setHeaderLength((byte) 11);
                    resp.setHeaderEncoding((byte) 0);
                    resp.setPacketIdentifier((short) (egtsPkg.getPacketIdentifier() + 1));
                    resp.setPacketType(EgtsPacketType.PT_RESPONSE);
                    resp.setServicesFrameData(pt);

                    byte[] buf = resp.encode();
                    output.write(buf);
                }
            }
        } catch (IOException e) {
            logger.error("Ошибка в обработке", e);
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }
}