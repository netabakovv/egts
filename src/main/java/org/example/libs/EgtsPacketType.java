package org.example.libs;

public enum EgtsPacketType implements Codeable{
    PT_APP_DATA(1),
    PT_RESPONSE(0);

    private final int code;

    EgtsPacketType(int code) {
        this.code = code;
    }

    @Override
    public int getCode() {
        return code;
    }

    public static final byte PT_APP_DATA_CODE = (byte) PT_APP_DATA.getCode();
    public static final byte PT_RESPONSE_CODE = (byte) PT_RESPONSE.getCode();

    private static final EgtsPacketType[] LOOKUP = new EgtsPacketType[256];

    static {
        for (var type : values()) {
            LOOKUP[type.code] = type;
        }
    }

    public static EgtsPacketType fromCode(int code) {
        if (code < 0 || code >= LOOKUP.length || LOOKUP[code] == null) {
            throw new IllegalArgumentException("Unknown packet type code: " + code);
        }
        return LOOKUP[code];
    }
}