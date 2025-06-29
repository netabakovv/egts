package org.example.libs;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class EgtsPackage implements BinaryData {
    // Поля заголовка
    private byte protocolVersion;
    private byte securityKeyId;
    private String prefix;
    private String route;
    private String encryptionAlg;
    private String compression;
    private String priority;
    private byte headerLength;
    private byte headerEncoding;
    private short frameDataLength;
    private short packetIdentifier;
    private EgtsPacketType packetType;
    private short peerAddress;
    private short recipientAddress;
    private byte timeToLive;
    private byte headerCheckSum;
    private BinaryData servicesFrameData;
    private short servicesFrameDataCheckSum;

    public enum DecodeResultCode {
        OK,
        INCORRECT_HEADER_FORMAT,
        HEADER_CRC_ERROR,
        DECRYPTION_ERROR,
        UNSUPPORTED_PACKET_TYPE,
        INCOMPLETE_DATA_FORMAT
    }

    public record DecodeResult(DecodeResultCode code, String message, EgtsPackage pkg) {}

    // Options
    public record EncodeOptions(SecretKey secretKey) {}
    public record DecodeOptions(SecretKey secretKey) {}

    @Override
    public void decode(byte[] data) throws IOException {
        ByteBuffer buf = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);

        this.protocolVersion = buf.get();
        this.securityKeyId = buf.get();

        byte flags = buf.get();
        parseFlags(flags);

        this.headerLength = buf.get();
        this.headerEncoding = buf.get();

        this.frameDataLength = buf.getShort();
        this.packetIdentifier = buf.getShort();
        this.packetType = EgtsPacketType.fromCode(buf.get());

        if ("1".equals(route)) {
            this.peerAddress = buf.getShort();
            this.recipientAddress = buf.getShort();
            this.timeToLive = buf.get();
        }

        this.headerCheckSum = buf.get();

        // Проверка CRC8 заголовка
        int headerEndPos = buf.position();
        byte[] headerBytes = new byte[headerEndPos];
        System.arraycopy(data, 0, headerBytes, 0, headerEndPos);
        byte calculatedHcs = CRC.crc8(headerBytes);

        if (calculatedHcs != headerCheckSum) {
            throw new IOException("CRC8 заголовка неверен");
        }

        // Чтение тела пакета
        byte[] sfrd = new byte[frameDataLength];
        buf.get(sfrd);

        boolean isEncrypted = !"00".equals(encryptionAlg);
        if (isEncrypted && servicesFrameData != null) {
            // Здесь можно добавить расшифровку, если есть ключ
            throw new IOException("Шифрование не поддерживается в текущей версии");
        }

        // Выбор типа данных сервиса
        switch (packetType) {
            case PT_APP_DATA -> servicesFrameData = new ServiceDataSet();
            case PT_RESPONSE -> servicesFrameData = new PtResponse();
            default -> throw new IOException("Неизвестный тип пакета: " + packetType);
        }

        servicesFrameData.decode(sfrd);

        if (buf.remaining() >= 2) {
            this.servicesFrameDataCheckSum = buf.getShort();
            short expectedCrc = CRC.crc16(sfrd);
            if (expectedCrc != servicesFrameDataCheckSum) {
                throw new IOException("CRC16 тела пакета неверен");
            }
        } else {
            throw new IOException("Недостаточно данных для чтения CRC16");
        }
    }

    @Override
    public byte[] encode() throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(1024).order(ByteOrder.LITTLE_ENDIAN);

        buf.put(protocolVersion);
        buf.put(securityKeyId);

        // Собираем флаги
        long flagBits = Long.parseLong(prefix + route + encryptionAlg + compression + priority, 2);
        buf.put((byte) flagBits);

        if (headerLength == 0) {
            headerLength = 11;
            if ("1".equals(route)) {
                headerLength += 5;
            }
        }
        buf.put(headerLength);
        buf.put(headerEncoding);

        byte[] sfrd = new byte[0];
        if (servicesFrameData != null) {
            sfrd = servicesFrameData.encode();
            frameDataLength = (short) sfrd.length;
        }

        buf.putShort(frameDataLength);
        buf.putShort(packetIdentifier);
        buf.put((byte) packetType.getCode());

        if ("1".equals(route)) {
            buf.putShort(peerAddress);
            buf.putShort(recipientAddress);
            buf.put(timeToLive);
        }

        // CRC8 заголовка
        int headerEndPos = buf.position();
        byte[] headerBytes = new byte[headerEndPos];
        buf.rewind();
        buf.get(headerBytes);
        buf.position(headerEndPos);

        byte calculatedHcs = CRC.crc8(headerBytes);
        buf.put(calculatedHcs);

        // Добавляем данные сервиса
        if (sfrd.length > 0) {
            buf.put(sfrd);
            short crc16 = CRC.crc16(sfrd);
            buf.putShort(crc16);
        }

        byte[] result = new byte[buf.position()];
        buf.rewind();
        buf.get(result);
        return result;
    }

    @Override
    public int length() {
        try {
            return encode().length;
        } catch (IOException e) {
            return 0;
        }
    }

    /**
     * Парсит флаги из одного байта.
     */
    private void parseFlags(byte flagsByte) {
        int flags = Byte.toUnsignedInt(flagsByte);

        prefix = String.format("%02d", (flags >> 6) & 0x03); // PRF
        route = String.valueOf((flags >> 5) & 0x01);         // RTE
        encryptionAlg = String.format("%02d", (flags >> 3) & 0x03); // ENA
        compression = String.valueOf((flags >> 2) & 0x01);   // CMP
        priority = String.format("%02d", flags & 0x03);     // PR
    }
}