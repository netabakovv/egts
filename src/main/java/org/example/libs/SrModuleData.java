package org.example.libs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * SrModuleData — EGTS_SR_MODULE_DATA
 * Подзапись сервиса EGTS_AUTH_SERVICE.
 * Используется для передачи информации об инфраструктуре на стороне АСН, о составе,
 * состоянии и параметрах блоков и модулей АСН.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SrModuleData {
    private byte moduleType;        // MT — Module Type
    private int vendorID;           // VID — Vendor Identifier
    private short firmwareVersion;  // FWV — Firmware Version
    private short softwareVersion;  // SWV — Software Version
    private byte modification;      // MD — Modification
    private byte state;             // ST — State
    private String serialNumber;    // SRN — Serial Number
    private String description;     // DSCR — Description

    private static final byte DELIMITER = 0x00;
    private static final int TEXT_SECTION_SIZE = 10;

    /**
     * Декодирует байты в структуру SrModuleData.
     */
    public void decode(byte[] content) throws Exception {
        if (content == null || content.length < 14) {
            throw new IllegalArgumentException("Недостаточно данных для декодирования SrModuleData");
        }

        ByteBuffer buf = ByteBuffer.wrap(content);

        this.moduleType = buf.get();

        this.vendorID = buf.getInt(); // UINT32 (Little Endian)
        this.firmwareVersion = buf.getShort(); // USHORT (Little Endian)
        this.softwareVersion = buf.getShort(); // USHORT (Little Endian)
        this.modification = buf.get();
        this.state = buf.get();

        // Чтение SerialNumber
        StringBuilder srnBuilder = new StringBuilder();
        for (int i = 0; i < TEXT_SECTION_SIZE; i++) {
            byte b = buf.get();
            if (b == DELIMITER) break;
            srnBuilder.append((char) b);
        }
        this.serialNumber = srnBuilder.toString();

        // Пропускаем возможные оставшиеся байты до разделителя
        while (buf.hasRemaining() && buf.get() != DELIMITER) {}

        // Чтение Description
        StringBuilder dscrBuilder = new StringBuilder();
        while (buf.hasRemaining()) {
            byte b = buf.get();
            if (b == DELIMITER) break;
            dscrBuilder.append((char) b);
        }
        this.description = dscrBuilder.toString();
    }

    /**
     * Кодирует структуру в массив байт.
     */
    public byte[] encode() throws Exception {
        ByteBuffer buf = ByteBuffer.allocate(calculateEncodedLength());

        buf.put(moduleType);
        buf.putInt(vendorID);
        buf.putShort(firmwareVersion);
        buf.putShort(softwareVersion);
        buf.put(modification);
        buf.put(state);

        // Пишем SerialNumber
        if (serialNumber != null) {
            for (byte b : serialNumber.getBytes(StandardCharsets.UTF_8)) {
                buf.put(b);
            }
        }
        buf.put(DELIMITER);

        // Пишем Description
        if (description != null) {
            for (byte b : description.getBytes(StandardCharsets.UTF_8)) {
                buf.put(b);
            }
        }
        buf.put(DELIMITER);

        byte[] result = new byte[buf.position()];
        buf.rewind();
        buf.get(result);

        return result;
    }

    private int calculateEncodedLength() {
        int length = 1 + 4 + 2 + 2 + 1 + 1; // MT + VID + FWV + SWV + MD + ST

        if (serialNumber != null) length += serialNumber.length();
        length += 1; // Delimiter после SRN

        if (description != null) length += description.length();
        length += 1; // Delimiter после DSCR

        return length;
    }

    /**
     * Возвращает длину закодированной подзаписи.
     */
    public int length() {
        try {
            return encode().length;
        } catch (Exception e) {
            return 0;
        }
    }
}