package org.example.libs;

import lombok.Data;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Data
public class EgtsPackage {
    private byte protocolVersion;           // Версия протокола
    private byte securityKeyId;             // Идентификатор ключа безопасности
    private String prefix;                  // Префикс
    private String route;                   // Маршрут
    private String encryptionAlg;           // Алгоритм шифрования
    private String compression;             // Компрессия
    private String priority;                // Приоритет
    private byte headerLength;             // Длина заголовка
    private byte headerEncoding;            // Кодировка заголовка
    private short frameDataLength;          // Длина секции данных
    private short packetIdentifier;         // Идентификатор пакета
    private EgtsPacketType packetType;               // Тип пакета
    private short peerAddress;              // Адрес отправителя
    private short recipientAddress;         // Адрес получателя
    private byte timeToLive;               // Время жизни пакета
    private byte headerCheckSum;           // Контрольная сумма заголовка
    private BinaryData servicesFrameData;  // Данные сервиса
    private short servicesFrameDataCheckSum; // Контрольная сумма данных сервиса

    public record EncodeOptions(SecretKey secretKey) {}
    public record DecodeOptions(SecretKey secretKey) {}

    public enum DecodeResultCode {
        OK,
        INCORRECT_HEADER_FORMAT,
        HEADER_CRC_ERROR,
        DECRYPTION_ERROR,
        UNSUPPORTED_PACKET_TYPE,
        INCOMPLETE_DATA_FORMAT
    }

    public record DecodeResult(DecodeResultCode code, String message, EgtsPackage pkg) {}

    public byte[] encode(EncodeOptions... options) throws IOException {
        var opt = new EncodeOptions(null);
        if (options.length > 0) opt = options[0];

        ByteBuffer buf = ByteBuffer.allocate(1024).order(ByteOrder.LITTLE_ENDIAN);
        SecretKey secretKey = opt.secretKey();

        buf.put(protocolVersion);
        buf.put(securityKeyId);

        int flags = parseFlags(prefix, route, encryptionAlg, compression, priority);
        buf.put((byte) flags);

        if (headerLength == 0) {
            headerLength = 11; // DEFAULT_HEADER_LEN
            if ("1".equals(route)) headerLength += 5;
        }
        buf.put(headerLength);
        buf.put(headerEncoding);

        byte[] sfrd = new byte[0];
        if (servicesFrameData != null) {
            sfrd = servicesFrameData.encode();
            if (!"00".equals(encryptionAlg) && secretKey != null) {
                sfrd = secretKey.encode();
            }
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

        int headerEndPos = buf.position();
        byte[] headerBytes = new byte[headerEndPos];
        buf.rewind();
        buf.get(headerBytes);
        buf.position(headerEndPos);

        byte calculatedHCS = CRC.crc8(headerBytes);
        buf.put(calculatedHCS);

        if (frameDataLength > 0) {
            buf.put(sfrd);
            short crc16 = CRC.crc16(sfrd);
            servicesFrameDataCheckSum = crc16;
            buf.putShort(crc16);
        }

        byte[] result = new byte[buf.position()];
        buf.rewind();
        buf.get(result);
        return result;
    }

    public DecodeResult decode(byte[] content, DecodeOptions... options) {
        var opt = new DecodeOptions(null);
        if (options.length > 0) opt = options[0];

        SecretKey secretKey = opt.secretKey();
        ByteBuffer buf = ByteBuffer.wrap(content).order(ByteOrder.LITTLE_ENDIAN);

        try {
            protocolVersion = buf.get();
            securityKeyId = buf.get();

            byte flags = buf.get();
            parseFlags(flags);

            headerLength = buf.get();
            headerEncoding = buf.get();

            frameDataLength = buf.getShort();
            packetIdentifier = buf.getShort();
            packetType = EgtsPacketType.fromCode(buf.get());

            if ("1".equals(route)) {
                peerAddress = buf.getShort();
                recipientAddress = buf.getShort();
                timeToLive = buf.get();
            }

            int headerEndPos = buf.position();
            byte[] headerBytes = new byte[headerEndPos];
            System.arraycopy(content, 0, headerBytes, 0, headerEndPos);
            headerCheckSum = buf.get();
            byte calculatedHCS = CRC.crc8(headerBytes);
            if (headerCheckSum != calculatedHCS) {
                return new DecodeResult(DecodeResultCode.HEADER_CRC_ERROR, "CRC8 заголовка неверен", this);
            }

            byte[] dataFrameBytes = new byte[frameDataLength];
            buf.get(dataFrameBytes);

            boolean isEncrypted = !"00".equals(encryptionAlg);
            if (isEncrypted) {
                if (secretKey == null) {
                    return new DecodeResult(DecodeResultCode.DECRYPTION_ERROR, "Отсутствует ключь шифрования", this);
                }
                dataFrameBytes = secretKey.decode(dataFrameBytes);
            }

            switch (packetType) {
                case PT_APP_DATA -> servicesFrameData = new ServiceDataSet();
                case PT_RESPONSE -> servicesFrameData = new PtResponse();
                default -> {
                    return new DecodeResult(DecodeResultCode.UNSUPPORTED_PACKET_TYPE, "Неизвестный тип пакета", this);
                }
            }

            servicesFrameData.decode(dataFrameBytes);

            servicesFrameDataCheckSum = buf.getShort();
            short expectedCrc = CRC.crc16(dataFrameBytes);
            if (servicesFrameDataCheckSum != expectedCrc) {
                return new DecodeResult(DecodeResultCode.HEADER_CRC_ERROR, "CRC16 тела пакета неверен", this);
            }

            return new DecodeResult(DecodeResultCode.OK, "Успешно декодировано", this);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int parseFlags(String prefix, String route, String enc, String cmp, String pr) {
        String flagBits = prefix + route + enc + cmp + pr;
        return Integer.parseInt(flagBits, 2);
    }

    private void parseFlags(byte flagsByte) {
        int flags = Byte.toUnsignedInt(flagsByte);
        prefix = String.format("%2s", Integer.toBinaryString((flags >> 6) & 0x03)).replace(' ', '0');
        route = String.valueOf((flags >> 5) & 0x01);
        encryptionAlg = String.format("%2s", Integer.toBinaryString((flags >> 3) & 0x03)).replace(' ', '0');
        compression = String.valueOf((flags >> 2) & 0x01);
        priority = String.format("%2s", Integer.toBinaryString(flags & 0x03)).replace(' ', '0');
    }
}