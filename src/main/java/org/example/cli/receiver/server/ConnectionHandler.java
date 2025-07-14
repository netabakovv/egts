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
import java.util.Objects;

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
                logger.info("Получен пакет от {} байт, packetId={}", fullPacket.length, egtsPkg.getPacketIdentifier());
                Instant receivedTime = Instant.now();

                if (egtsPkg.getPacketType() == EgtsPacketType.PT_APP_DATA) {
                    ServiceDataSet sds = (ServiceDataSet) egtsPkg.getServicesFrameData();
                    for (var record : sds.getRecords()) {
                        // Переменные для накопления данных
                        long clientId = 0;
                        if (!Objects.equals(record.getObjectIDFieldExists(), "0")){
                            clientId = record.getObjectIdentifier();
                        }
                        int packetId = egtsPkg.getPacketIdentifier();
                        long navigationTs = 0;
                        long receivedTs = receivedTime.getEpochSecond();
                        double latitude = 0, longitude = 0;
                        int speed = 0, pdop = 0, hdop = 0, vdop = 0, nsat = 0, ns = 0, course = 0;
                        List<AnSensor> anSensors = new ArrayList<>();
                        List<LiquidSensor> liquidSensors = new ArrayList<>();
                        boolean hasPosition = false;

                        for (var rd : record.getRecordDataSet().getRecords()) {
                            switch (rd.getSubrecordType()) {
                                case RecordDataSet.SrPosDataType -> {
                                    var pos = (SrPosData) rd.getSubrecordData();
                                    navigationTs = pos.getNavigationTime().getEpochSecond();
                                    latitude = pos.getLatitude();
                                    longitude = pos.getLongitude();
                                    speed = pos.getSpeed();
                                    course = pos.getDirection() & 0XFF;
                                    hasPosition = true;
                                }
                                case RecordDataSet.SrTermIdentityType -> {
                                    var term = (SrTermIdentity) rd.getSubrecordData();
                                    clientId = term.getTerminalIdentifier();
                                }
                                case RecordDataSet.SrAdSensorsDataType -> {
                                    var ad = (SrAdSensorsData) rd.getSubrecordData();
                                    if ("1".equals(ad.getAnalogSensorFieldExists1()))
                                        anSensors.add(AnSensor.of(1, ad.getAnalogSensor1()));
                                    if ("1".equals(ad.getAnalogSensorFieldExists2()))
                                        anSensors.add(AnSensor.of(2, ad.getAnalogSensor2()));
                                    if ("1".equals(ad.getAnalogSensorFieldExists3()))
                                        anSensors.add(AnSensor.of(3, ad.getAnalogSensor3()));
                                    if ("1".equals(ad.getAnalogSensorFieldExists4()))
                                        anSensors.add(AnSensor.of(4, ad.getAnalogSensor4()));
                                    if ("1".equals(ad.getAnalogSensorFieldExists5()))
                                        anSensors.add(AnSensor.of(5, ad.getAnalogSensor5()));
                                    if ("1".equals(ad.getAnalogSensorFieldExists6()))
                                        anSensors.add(AnSensor.of(6, ad.getAnalogSensor6()));
                                    if ("1".equals(ad.getAnalogSensorFieldExists7()))
                                        anSensors.add(AnSensor.of(7, ad.getAnalogSensor7()));
                                    if ("1".equals(ad.getAnalogSensorFieldExists8()))
                                        anSensors.add(AnSensor.of(8, ad.getAnalogSensor8()));
                                }
                                case RecordDataSet.SrLiquidLevelSensorType -> {
                                    var fuel = (SrLiquidLevelSensor) rd.getSubrecordData();
                                    liquidSensors.add(
                                            LiquidSensor.of(
                                                    fuel.getLiquidLevelSensorNumber(),
                                                    fuel.getLiquidLevelSensorErrorFlag(),
                                                    fuel.getLiquidLevelSensorData(),
                                                    Long.parseLong(fuel.getLiquidLevelSensorValueUnit())
                                            )
                                    );
                                }
                                case RecordDataSet.SrExtPosDataType -> {
                                    var ext = (SrExtPosData) rd.getSubrecordData();
                                    logger.info("SrExtPosData: pdop={}, hdop={}, vdop={}, nsat={}, ns={}",
                                            ext.getPdop(), ext.getHdop(), ext.getVdop(), ext.getSatellites(), ext.getNavigationSystem());

                                    pdop = ext.getPdop(); hdop = ext.getHdop(); vdop = ext.getVdop();
                                    nsat = ext.getSatellites(); ns = ext.getNavigationSystem();
                                }
                                case RecordDataSet.SrStateDataType -> {
                                    var state = (SrStateData) rd.getSubrecordData();
                                    logger.info("Состояние АСН: режим={}, напряжение={}V",
                                            state.getState(), state.getMainPowerSourceVoltage());
                                }
//                                case RecordDataSet.SrAccelDataType -> {
//                                    var accel = (SensAccelerometerData) rd.getSubrecordData();
//                                    // TODO: обработка данных акселерометра (accel.getX(), getY(), getZ())
//                                }
////                                case RecordDataSet.SrLoopInDataType -> {
////                                    var loop = (SrLoopinData) rd.getSubrecordData();
////                                    // TODO: обработка статусов петлевых входов (loop.getLoopinStates())
////                                }
//                                case RecordDataSet.SrAbsDigSensDataType -> {
//                                    var dig = (SensButtonPressCounter) rd.getSubrecordData();
//                                    anSensors.add(AnSensor.of(dig.getSensNum(), dig.getValue()));
//                                }
//                                case RecordDataSet.SrCountersDataType -> {
//                                    // TODO Раздефать
//                                    var cnts = (SrCountersData) rd.getSubrecordData();
//                                    for (var c : cnts.get()) {
//                                        anSensors.add(AnSensor.of(c.getCounterNumber(), c.getCounterValue()));
//                                    }
//                                }
////                                case RecordDataSet.SrAbsLoopInDataType -> {
////                                    var absLoop = (SrAbsLoopinData) rd.getSubrecordData();
////                                    // TODO: обработка одного петлевого входа absLoop.getLoopinNumber(), getFlag()
////                                }
//                                case RecordDataSet.SrPassengersCntrType -> {
//                                    var pas = (SrPassengersCountersData) rd.getSubrecordData();
//                                    // TODO: обработка пассажирских счётчиков pas.getCounters()
//                                }
                                default -> logger.info("Неизвестная подзапись: {}", rd.getSubrecordType());
                            }
                        }
                        if (hasPosition) {
                            NavRecord nav = new NavRecord(
                                    clientId,
                                    packetId,
                                    navigationTs,
                                    receivedTs,
                                    latitude,
                                    longitude,
                                    speed,
                                    pdop,
                                    hdop,
                                    vdop,
                                    nsat,
                                    ns,
                                    course,
                                    anSensors,
                                    liquidSensors
                            );
                            storage.save(nav);
                            logger.debug("Сохранен NavRecord: {}", nav);
                        }
                    }

                    // Ответ
                    PtResponse pt = new PtResponse();
                    pt.setResponsePacketID((short) egtsPkg.getPacketIdentifier());
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