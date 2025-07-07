package org.example.libs;

public enum EgtsService{
    AUTH_SERVICE((byte) 1),
    TELEDATA_SERVICE((byte) 2);

    private final int code;

    EgtsService(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    private static final EgtsService[] LOOKUP = new EgtsService[256];

    static {
        for (var service : values()) {
            LOOKUP[service.code] = service;
        }
    }

    public static EgtsService fromCode(byte code) {
        for (EgtsService t : values()) {
            if (t.code == code) return t;
        }
        throw new IllegalArgumentException("Unknown packet type: " + code);
    }
}
