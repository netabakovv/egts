package org.example.cli.receiver.storage;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

/**
 * Представляет навигационную запись EGTS-пакета.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record NavRecord(
        @JsonProperty("client") long client,
        @JsonProperty("packet_id") long packetID,
        @JsonProperty("navigation_unix_time") long navigationTimestamp,
        @JsonProperty("received_unix_time") long receivedTimestamp,
        @JsonProperty("latitude") double latitude,
        @JsonProperty("longitude") double longitude,
        @JsonProperty("speed") int speed,
        @JsonProperty("pdop") int pdop,
        @JsonProperty("hdop") int hdop,
        @JsonProperty("vdop") int vdop,
        @JsonProperty("nsat") int nsat,
        @JsonProperty("ns") int ns,
        @JsonProperty("course") int course,
        @JsonProperty("an_sensors") List<AnSensor> anSensors,
        @JsonProperty("liquid_sensors") List<LiquidSensor> liquidSensors) implements Serializable{

    /**
     * Сериализует объект в JSON-байты
     *
     * @return массив байтов в формате JSON
     */
    @Override
    public byte[] toBytes() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsBytes(this);
    }

    /**
     * Статический метод для создания экземпляра NavRecord
     */
    public static NavRecord of(
            long client,
            long packetID,
            long navigationTimestamp,
            long receivedTimestamp,
            double latitude,
            double longitude,
            int speed,
            int pdop,
            int hdop,
            int vdop,
            int nsat,
            int ns,
            int course,
            List<AnSensor> anSensors,
            List<LiquidSensor> liquidSensors) {
        return new NavRecord(client, packetID, navigationTimestamp, receivedTimestamp,
                latitude, longitude, speed, pdop, hdop, vdop, nsat, ns, course, anSensors, liquidSensors);
    }
}
