package org.example.libs;

import lombok.Getter;
import lombok.Setter;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecordDataSet implements BinaryData {

    @Getter
    @Setter
    public static class RecordData {
        private byte subrecordType;
        private short subrecordLength;
        private BinaryData subrecordData;

        @Override
        public String toString() {
            return "RecordData{" +
                    "type=" + subrecordType +
                    ", length=" + subrecordLength +
                    ", data=" + subrecordData +
                    '}';
        }
    }

    private final List<RecordData> records = new ArrayList<>();

    public List<RecordData> getRecords() {
        return Collections.unmodifiableList(records);
    }

    public void addRecord(RecordData rd) {
        records.add(rd);
    }

    @Override
    public void decode(byte[] recDS) throws IOException {
        ByteBuffer buf = ByteBuffer.wrap(recDS).order(ByteOrder.LITTLE_ENDIAN);

        while (buf.hasRemaining()) {
            RecordData rd = new RecordData();
            rd.setSubrecordType(buf.get());

            short length = buf.getShort();
            rd.setSubrecordLength(length);

            byte[] subRecordBytes = new byte[length];
            buf.get(subRecordBytes);

            BinaryData subrecord = createSubrecord(rd.getSubrecordType(), length);
            if (subrecord == null) {
                System.out.printf("Unknown subrecord type: %d, length: %d%n", rd.getSubrecordType(), length);
                continue;
            }

            subrecord.decode(subRecordBytes);
            rd.setSubrecordData(subrecord);
            records.add(rd);
        }
    }

    @Override
    public byte[] encode() throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(baos)) {

            for (RecordData rd : records) {
                byte type = rd.getSubrecordType();

                if (type == 0 && rd.getSubrecordData() != null) {
                    type = detectSubrecordType(rd.getSubrecordData());
                }

                if (rd.getSubrecordData() == null) {
                    throw new IOException("Missing subrecordData for type: " + type);
                }

                byte[] srdBytes = rd.getSubrecordData().encode();
                short length = (short) srdBytes.length;

                dos.writeByte(type);
                dos.writeShort(Short.toUnsignedInt(length)); // ensure 2 bytes
                dos.write(srdBytes);
            }

            return baos.toByteArray();
        }
    }

    @Override
    public int length() {
        try {
            return (short) encode().length;
        } catch (Exception e) {
            return 0;
        }
    }

    // ======================
    // ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ
    // ======================

    private BinaryData createSubrecord(byte type, short length) {
        return switch (type) {
            case SrPosDataType -> new SrPosData();
            case SrTermIdentityType -> new SrTermIdentity();
            case SrModuleDataType -> new SrModuleData();
            case SrRecordResponseType -> new SrResponse();
            case SrResultCodeType -> new SrResultCode();
            case SrExtPosDataType -> new SrExtPosData();
            case SrAdSensorsDataType -> new SrAdSensorsData();
            case SrType20 -> (length == 5) ? new SrStateData() : null;
            case SrStateDataType -> new SrStateData();
            case SrLiquidLevelSensorType -> new SrLiquidLevelSensor();
            case SrAbsCntrDataType -> new SrAbsCntrData();
            case SrAuthInfoType -> new SrAuthInfo();
            case SrCountersDataType -> new SrCountersData();
            case SrEgtsPlusDataType -> new StorageRecordWrapper(); // Обертка над сгенерированным StorageRecord
            case SrAbsAnSensDataType -> new SrAbsSensData();
            case SrDispatcherIdentityType -> new SrDispatcherIdentity();
            case SrPassengersCountersType -> new SrPassengersCountersData();
            default -> null;
        };
    }

    private byte detectSubrecordType(BinaryData data) throws IOException {
        if (data instanceof SrPosData) return SrPosDataType;
        if (data instanceof SrTermIdentity) return SrTermIdentityType;
        if (data instanceof SrResponse) return SrRecordResponseType;
        if (data instanceof SrResultCode) return SrResultCodeType;
        if (data instanceof SrExtPosData) return SrExtPosDataType;
        if (data instanceof SrAdSensorsData) return SrAdSensorsDataType;
        if (data instanceof SrStateData) return SrStateDataType;
        if (data instanceof SrLiquidLevelSensor) return SrLiquidLevelSensorType;
        if (data instanceof SrAbsCntrData) return SrAbsCntrDataType;
        if (data instanceof SrAuthInfo) return SrAuthInfoType;
        if (data instanceof SrCountersData) return SrCountersDataType;
        if (data instanceof StorageRecordWrapper) return SrEgtsPlusDataType;  // Обертка над сгенерированным StorageRecord
        if (data instanceof SrAbsSensData) return SrAbsAnSensDataType;
        if (data instanceof SrDispatcherIdentity) return SrDispatcherIdentityType;
        if (data instanceof SrPassengersCountersData) return SrPassengersCountersType;
        throw new IOException("Unknown subrecord type for class: " + data.getClass().getSimpleName());
    }

    // ======================
    // КОНСТАНТЫ
    // ======================

    public static final byte SrRecordResponseType = 0;
    public static final byte SrTermIdentityType = 1;
    public static final byte SrModuleDataType = 2;
    public static final byte SrAuthInfoType = 7;
    public static final byte SrResultCodeType = 9;
    public static final byte SrDispatcherIdentityType = 5;
    public static final byte SrEgtsPlusDataType = 15;
    public static final byte SrPosDataType = 16;
    public static final byte SrExtPosDataType = 17;
    public static final byte SrAdSensorsDataType = 18;
    public static final byte SrCountersDataType = 19;
    public static final byte SrType20 = 20;
    public static final byte SrStateDataType = 21;
    public static final byte SrLiquidLevelSensorType = 27;
    public static final byte SrAbsCntrDataType = 25;
    public static final byte SrAbsAnSensDataType = 24;
    public static final byte SrPassengersCountersType = 28;

    // Пакеты
    public static final byte PtAppdataPacket = 1;
    public static final byte PtResponsePacket = 0;

    // Сервисы
    public static final byte AuthService = 1;
    public static final byte TeledataService = 2;
}
