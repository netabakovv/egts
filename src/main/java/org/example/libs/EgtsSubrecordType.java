package org.example.libs;

public enum EgtsSubrecordType implements Codeable{
    EGTS_SR_RECORD_RESPONSE(0),
    EGTS_SR_TERM_IDENTITY(1),
    EGTS_SR_MODULE_DATA(2),
    EGTS_SR_RESULT_CODE(9),
    EGTS_SR_AUTH_INFO(7),
    EGTS_SR_EGTS_PLUS_DATA(15),
    EGTS_SR_POS_DATA(16),
    EGTS_SR_EXT_POS_DATA(17),
    EGTS_SR_AD_SENSORS_DATA(18),
    EGTS_SR_COUNTERS_DATA(19),
    EGTS_SR_STATE_DATA(20),
    EGTS_SR_ACCEL_DATA(21),
    EGTS_SR_LOOPIN_DATA(22),
    EGTS_SR_ABS_DIG_SENS_DATA(23),
    EGTS_SR_ABS_AN_SENS_DATA(24),
    EGTS_SR_ABS_CNTR_DATA(25),
    EGTS_SR_ABS_LOOPIN_DATA(26),
    EGTS_SR_LIQUID_LEVEL_SENSOR(27),
    EGTS_SR_PASSENGERS_COUNTERS(28),
    EGTS_SR_DISPATCHER_IDENTITY(5);

    private final int code;

    EgtsSubrecordType(int code) {
        this.code = code;
    }

    @Override
    public int getCode() {
        return code;
    }

    private static final EgtsSubrecordType[] LOOKUP = new EgtsSubrecordType[256];

    static {
        for (var type : values()) {
            LOOKUP[type.code] = type;
        }
    }

    public static EgtsSubrecordType fromCode(int code) {
        if (code < 0 || code >= LOOKUP.length || LOOKUP[code] == null) {
            throw new IllegalArgumentException("Unknown subrecord type code: " + code);
        }
        return LOOKUP[code];
    }
}