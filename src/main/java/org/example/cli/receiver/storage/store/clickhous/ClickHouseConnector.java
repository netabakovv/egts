package org.example.cli.receiver.storage.store.clickhous;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.cli.receiver.storage.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ClickHouseConnector implements Store<Serializable> {

    private final Connection connection;
    private final String database;
    private final String table;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public ClickHouseConnector(Map<String, String> config) throws SQLException {
        if (config == null || config.isEmpty()) {
            throw new IllegalArgumentException("Настройки ClickHouse отсутствуют");
        }

        String host = config.getOrDefault("host", "localhost");
        String port = config.getOrDefault("port", "8123");
        this.database = config.getOrDefault("database", "default");
        this.table = config.getOrDefault("table", "nav_records");

        String jdbcUrl = String.format("jdbc:clickhouse://%s:%s/%s", host, port, database);

        this.connection = DriverManager.getConnection(
                jdbcUrl,
                config.getOrDefault("user", "default"),
                config.getOrDefault("password", "")
        );

        System.out.println("Подключились к ClickHouse");

        createTableIfNotExists();
    }

    @Override
    public void init(Map<String, String> config) throws IOException {
        // Инициализация уже выполнена в конструкторе
    }

    @Override
    public void save(Serializable data) throws IOException {
        try {
            if (data instanceof NavRecord record) {
                String sql = String.format("INSERT INTO %s.%s (" +
                                "client, packet_id, navigation_unix_time, received_unix_time, latitude, longitude, speed, pdop, hdop, vdop, nsat, ns, course, an_sensors, liquid_sensors, raw_data" +
                                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        database, table);

                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.setLong(1, record.client());
                    stmt.setLong(2, record.packetID());
                    stmt.setLong(3, record.navigationTimestamp());
                    stmt.setLong(4, record.receivedTimestamp());
                    stmt.setDouble(5, record.latitude());
                    stmt.setDouble(6, record.longitude());
                    stmt.setInt(7, record.speed());
                    stmt.setInt(8, record.pdop());
                    stmt.setInt(9, record.hdop());
                    stmt.setInt(10, record.vdop());
                    stmt.setInt(11, record.nsat());
                    stmt.setInt(12, record.ns());
                    stmt.setInt(13, record.course());

                    stmt.setString(14, serializeAnSensors(record.anSensors()));
                    stmt.setString(15, serializeLiquidSensors(record.liquidSensors()));
                    stmt.setString(16, serializeRaw(record));

                    stmt.executeUpdate();
                }
            } else {
                throw new IOException("Unsupported data type: " + data.getClass().getName());
            }
        } catch (SQLException | JsonProcessingException e) {
            throw new IOException("Ошибка при сохранении в ClickHouse", e);
        }
    }

    @Override
    public void close() throws IOException {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new IOException("Ошибка при закрытии соединения с ClickHouse", e);
        }
    }

    private void createTableIfNotExists() throws SQLException {
        String createTableSQL = String.format(
                "CREATE TABLE IF NOT EXISTS %s.%s (" +
                        "client UInt32, " +
                        "packet_id UInt32, " +
                        "navigation_unix_time Int64, " +
                        "received_unix_time Int64, " +
                        "latitude Float64, " +
                        "longitude Float64, " +
                        "speed UInt16, " +
                        "pdop UInt16, " +
                        "hdop UInt16, " +
                        "vdop UInt16, " +
                        "nsat UInt8, " +
                        "ns UInt16, " +
                        "course UInt8, " +
                        "an_sensors Array(Tuple(sensor_number UInt8, value UInt32)), " +
                        "liquid_sensors Array(Tuple(sensor_number UInt8, error_flag String, value_mm UInt32, value_l UInt32)), " +
                        "raw_data String" +
                        ") ENGINE = MergeTree() " +
                        "ORDER BY (client, navigation_unix_time)",
                database, table);

        try (var stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("Таблица " + table + " проверена/создана");
        }
    }

    private String serializeAnSensors(List<AnSensor> sensors) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; sensors != null && i < sensors.size(); i++) {
            if (i > 0) sb.append(',');
            var sensor = sensors.get(i);
            sb.append(String.format("(%.0f,%.0f)", (double) sensor.sensorNumber(), (double) sensor.value()));
        }
        return sb.append(']').toString();
    }

    private String serializeLiquidSensors(List<LiquidSensor> sensors) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; sensors != null && i < sensors.size(); i++) {
            if (i > 0) sb.append(',');
            var sensor = sensors.get(i);
            sb.append(String.format("(%.0f,'%s',%.0f,%.0f)",
                    (double) sensor.sensorNumber(),
                    sensor.errorFlag(),
                    (double) sensor.valueMm(),
                    (double) sensor.valueL()));
        }
        return sb.append(']').toString();
    }

    private String serializeRaw(Serializable data) throws JsonProcessingException {
        return objectMapper.writeValueAsString(data);
    }
}