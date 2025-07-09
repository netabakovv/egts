package org.example.libs;

/**
 * Типы пакетов протокола EGTS согласно ГОСТ 33472-2015
 */
public enum EgtsPacketType {
    PT_RESPONSE((byte) 0x00, "Пакет-ответ"),
    PT_APP_DATA((byte) 0x01, "Пакет прикладных данных"),
    PT_SIGNED_APP_DATA((byte) 0x02, "Подписанный пакет прикладных данных"),
    PT_AUTH((byte) 0x03, "Пакет аутентификации"),
    PT_SERVICE_DATA((byte) 0x04, "Пакет сервисных данных"),
    PT_RESERVED_1((byte) 0x05, "Зарезервировано"),
    PT_RESERVED_2((byte) 0x06, "Зарезервировано"),
    PT_RESERVED_3((byte) 0x07, "Зарезервировано"),
    PT_ERROR((byte) 0x08, "Пакет с ошибкой");

    private final byte code;
    private final String description;

    EgtsPacketType(byte code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * Получить числовой код типа пакета
     */
    public byte getCode() {
        return code;
    }

    /**
     * Получить описание типа пакета
     */
    public String getDescription() {
        return description;
    }

    private static final EgtsPacketType[] LOOKUP = new EgtsPacketType[256];

    static {
        for (EgtsPacketType type : values()) {
            LOOKUP[type.code & 0xFF] = type;
        }
    }

    /**
     * Получить тип пакета по его коду
     * @param code код типа пакета
     * @return соответствующий тип пакета
     * @throws IllegalArgumentException если код неизвестен
     */
    public static EgtsPacketType fromCode(int code) {
        if (code < 0 || code >= LOOKUP.length) {
            throw new IllegalArgumentException("Invalid packet type code: " + code);
        }

        EgtsPacketType type = LOOKUP[code];
        if (type == null) {
            throw new IllegalArgumentException("Unknown packet type code: " + code);
        }
        return type;
    }

    /**
     * Проверить, является ли пакет прикладным (содержит данные)
     */
    public boolean isAppData() {
        return this == PT_APP_DATA || this == PT_SIGNED_APP_DATA;
    }

    /**
     * Проверить, является ли пакет служебным
     */
    public boolean isService() {
        return this == PT_RESPONSE || this == PT_AUTH || this == PT_SERVICE_DATA || this == PT_ERROR;
    }
}