package org.example.cli.receiver.storage;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Датчик уровня жидкости.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record LiquidSensor(
        @JsonProperty("sensor_number") int sensorNumber,
        @JsonProperty("error_flag") String errorFlag,
        @JsonProperty("value_mm") long valueMm,
        @JsonProperty("value_l") long valueL) {

    public static LiquidSensor of(int sensorNumber, String errorFlag, long valueMm, long valueL) {
        return new LiquidSensor(sensorNumber, errorFlag, valueMm, valueL);
    }
}
