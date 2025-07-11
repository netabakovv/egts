package org.example.cli.receiver.storage.store.clickhous;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.cli.receiver.storage.*;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
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

        System.out.printf("pdop=%d, hdop=%d, vdop=%d, nsat=%d, ns=%d\n",
                record.pdop(), record.hdop(), record.vdop(), record.nsat(), record.ns());

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, record.client());
            stmt.setLong(2, record.packetID());
            stmt.setTimestamp(3, new Timestamp(record.navigationTimestamp() * 1000));
            stmt.setTimestamp(4, new Timestamp(record.receivedTimestamp() * 1000));
            stmt.setDouble(5, record.latitude());
            stmt.setDouble(6, record.longitude());
            stmt.setInt(7, record.speed());
            stmt.setShort(8, (short) record.pdop());
            stmt.setShort(9, (short) record.hdop());
            stmt.setShort(10, (short) record.vdop());
            stmt.setByte(11, (byte) record.nsat());
            stmt.setShort(12, (short) record.ns());
            stmt.setInt(13, record.course());
            stmt.setObject(14, serializeAnSensors(record.anSensors()));
            stmt.setObject(15, serializeLiquidSensors(record.liquidSensors()));
            stmt.setString(16, objectMapper.writeValueAsString(record));

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new IOException("Ошибка при сохранении в ClickHouse", e);
        }
    }

    private Object[] serializeAnSensors(List<AnSensor> sensors) {
        if (sensors == null || sensors.isEmpty()) {
            return new Object[0];
        }

        Object[] result = new Object[sensors.size()];
        for (int i = 0; i < sensors.size(); i++) {
            AnSensor sensor = sensors.get(i);
            result[i] = new Object[]{
                    (short) sensor.sensorNumber(),  // UInt8
                    sensor.value()                  // UInt32
            };
        }
        return result;
    }

    private Object[] serializeLiquidSensors(List<LiquidSensor> sensors) {
        if (sensors == null || sensors.isEmpty()) {
            return new Object[0];
        }

        Object[] result = new Object[sensors.size()];
        for (int i = 0; i < sensors.size(); i++) {
            LiquidSensor sensor = sensors.get(i);
            result[i] = new Object[]{
                    (short) sensor.sensorNumber(),  // UInt8
                    sensor.errorFlag(),             // String
                    sensor.valueMm(),               // UInt32
                    sensor.valueL()                 // UInt32
            };
        }
        return result;
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