package org.example.libs;

public enum EgtsProcessingCode {
    OK(0),
    IN_PROGRESS(1),
    UNSUPPORTED_PROTOCOL(128),
    DECRYPT_ERROR(129),
    PROCESSING_DENIED(130),
    INCORRECT_HEADER_FORMAT(131),
    INCORRECT_DATA_FORMAT(132),
    UNSUPPORTED_TYPE(133),
    NOT_ENOUGH_PARAMS(134),
    DOUBLE_PROCESSING(135),
    PROCESSING_SRC_DENIED(136),
    HEADER_CRC_ERROR(137),
    DATA_CRC_ERROR(138),
    INVALID_DATA_LENGTH(139),
    ROUTE_NOT_FOUND(140),
    ROUTE_CLOSED(141),
    ROUTE_DENIED(142),
    INVALID_ADDRESS(143),
    TTL_EXPIRED(144),
    NO_ACK(145),
    OBJECT_NOT_FOUND(146),
    EVENT_NOT_FOUND(147),
    SERVICE_NOT_FOUND(148),
    SERVICE_DENIED(149),
    SERVICE_UNKNOWN(150),
    AUTH_DENIED(151),
    ALREADY_EXISTS(152),
    ID_NOT_FOUND(153),
    INCORRECT_DATETIME(154),
    IO_ERROR(155),
    NO_RESOURCES_AVAILABLE(156),
    MODULE_FAULT(157),
    MODULE_PWR_FAULT(158),
    MODULE_PROC_FAULT(159),
    MODULE_SW_FAULT(160),
    MODULE_FW_FAULT(161),
    MODULE_IO_FAULT(162),
    MODULE_MEM_FAULT(163),
    TEST_FAILED(164);

    private final int code;

    EgtsProcessingCode(int code) {
        this.code = code;
    }

    public short getCode() {
        return (short) code;
    }

    public static EgtsProcessingCode fromCode(int code) {
        for (EgtsProcessingCode value : values()) {
            if (value.code == code) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown EGTS processing code: " + code);
    }
}
