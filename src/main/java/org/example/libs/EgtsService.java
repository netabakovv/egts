package org.example.libs;

public enum EgtsService implements Codeable {
    AUTH_SERVICE(1),
    TELEDATA_SERVICE(2);

    private final int code;

    EgtsService(int code) {
        this.code = code;
    }

    @Override
    public int getCode() {
        return code;
    }

    private static final EgtsService[] LOOKUP = new EgtsService[256];

    static {
        for (var service : values()) {
            LOOKUP[service.code] = service;
        }
    }

    public static EgtsService fromCode(int code) {
        if (code < 0 || code >= LOOKUP.length || LOOKUP[code] == null) {
            throw new IllegalArgumentException("Unknown service code: " + code);
        }
        return LOOKUP[code];
    }
}
