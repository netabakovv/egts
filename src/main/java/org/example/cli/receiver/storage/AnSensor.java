package org.example.cli.receiver.storage;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Аналоговый датчик.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AnSensor(
        @JsonProperty("sensor_number") int sensorNumber,
        @JsonProperty("value") long value) {

    public static AnSensor of(int sensorNumber, long value) {
        return new AnSensor(sensorNumber, value);
    }
}
