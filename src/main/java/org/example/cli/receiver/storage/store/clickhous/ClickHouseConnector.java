package org.example.cli.receiver.storage.store.clickhous;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.cli.receiver.storage.*;

import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ClickHouseConnector implements Store<Serializable> {

    private Connection connection;
    private final String database;
    private final String table;
    private final String host;
    private final String port;
    private final String user;
    private final String password;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public ClickHouseConnector() throws IOException {
        this(Map.of(
                "host", "localhost",
                "port", "8123",
                "user", "default",
                "password", "",
                "database", "receiver",
                "table", "points"
        ));
    }

    public ClickHouseConnector(Map<String, String> config) throws IOException {
        if (config == null) {
            throw new IllegalArgumentException("Настройки ClickHouse отсутствуют");
        }

        this.host = config.getOrDefault("host", "localhost");
        this.port = config.getOrDefault("port", "8123");
        this.user = config.getOrDefault("user", "default");
        this.password = config.getOrDefault("password", "");
        this.database = config.getOrDefault("database", "receiver");
        this.table = config.getOrDefault("table", "points");

        try {
            initializeDatabase();
            createTableIfNotExists();
            System.out.println("ClickHouse подключен: " + database + "." + table);
        } catch (SQLException | ClassNotFoundException e) {
            throw handleInitializationError(e);
        }
    }

    private void initializeDatabase() throws SQLException, ClassNotFoundException {
        Class.forName("com.clickhouse.jdbc.ClickHouseDriver");

        // 1. Подключаемся к системной базе default
        String systemUrl = String.format("jdbc:clickhouse://%s:%s/default", host, port);
        Properties props = new Properties();
        props.setProperty("user", user);
        if (!password.isEmpty()) {
            props.setProperty("password", password);
        }

        try (Connection sysConn = DriverManager.getConnection(systemUrl, props)) {
            // 2. Создаем целевую базу данных, если не существует
            try (Statement stmt = sysConn.createStatement()) {
                stmt.execute(String.format("CREATE DATABASE IF NOT EXISTS %s", database));
            }
        }

        // 3. Подключаемся к целевой базе данных
        String targetUrl = String.format("jdbc:clickhouse://%s:%s/%s", host, port, database);
        this.connection = DriverManager.getConnection(targetUrl, props);
    }

    private IOException handleInitializationError(Exception e) {
        if (e.getMessage().contains("Authentication failed")) {
            String errorMsg = "Ошибка аутентификации в ClickHouse\n";
            errorMsg += "Проверьте:\n";
            errorMsg += "1. Файл /etc/clickhouse-server/users.d/default-password.xml в контейнере\n";
            errorMsg += "2. Совпадение пароля в коде и контейнере\n";
            errorMsg += "3. Попробуйте сбросить пароль: docker exec -it <container> rm /etc/clickhouse-server/users.d/default-password.xml && docker restart <container>";

            return new IOException(errorMsg, e);
        }
        return new IOException("Ошибка инициализации ClickHouse: " + e.getMessage(), e);
    }

    private void createTableIfNotExists() throws SQLException {
        String createTableSQL = String.format("CREATE TABLE IF NOT EXISTS %s.%s (" +
                "client UInt32, " +
                "packet_id UInt32, " +
                "navigation_unix_time DateTime64(3), " +
                "received_unix_time DateTime64(3), " +
                "latitude Float64, " +
                "longitude Float64, " +
                "speed UInt16, " +
                "pdop UInt16, " +
                "hdop UInt16, " +
                "vdop UInt16, " +
                "nsat UInt8, " +
                "ns UInt16, " +
                "course UInt16, " +
                "an_sensors Array(Tuple(UInt8, UInt32)), " +
                "liquid_sensors Array(Tuple(UInt8, String, UInt32, UInt32)), " +
                "raw_data String" +
                ") ENGINE = MergeTree() " +
                "ORDER BY (client, navigation_unix_time)", database, table);

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
        }
    }

    // Остальные методы без изменений
    @Override
    public void save(Serializable data) throws IOException {
        if (!(data instanceof NavRecord)) {
            throw new IOException("Unsupported data type: " + data.getClass().getName());
        }

        NavRecord record = (NavRecord) data;
        String sql = String.format(
                "INSERT INTO %s.%s VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                database, table
        );

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
            stmt.setString(16, objectMapper.writeValueAsString(record));

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new IOException("Ошибка при сохранении в ClickHouse", e);
        }
    }

    private String serializeAnSensors(List<AnSensor> sensors) {
        if (sensors == null || sensors.isEmpty()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < sensors.size(); i++) {
            if (i > 0) sb.append(",");
            AnSensor sensor = sensors.get(i);
            sb.append(String.format("(%d,%d)", sensor.sensorNumber(), sensor.value()));
        }
        return sb.append("]").toString();
    }

    private String serializeLiquidSensors(List<LiquidSensor> sensors) {
        if (sensors == null || sensors.isEmpty()) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < sensors.size(); i++) {
            if (i > 0) sb.append(",");
            LiquidSensor sensor = sensors.get(i);
            sb.append(String.format("(%d,'%s',%d,%d)",
                    sensor.sensorNumber(),
                    sensor.errorFlag(),
                    sensor.valueMm(),
                    sensor.valueL()));
        }
        return sb.append("]").toString();
    }

    @Override
    public void close() throws IOException {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new IOException("Ошибка при закрытии соединения", e);
        }
    }

    @Override
    public void init(Map<String, String> config) throws IOException {
        // Уже инициализировано в конструкторе
    }
}