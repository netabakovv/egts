package org.example.cli.receiver.storage.store.postgresql;

import org.example.cli.receiver.storage.NavRecord;
import org.example.cli.receiver.storage.Store;
import org.example.cli.receiver.storage.Serializable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Map;

public class PGConnector implements Store<Serializable> {
    private final String database;
    private final String table;
    private Connection connection;

    public PGConnector(Map<String, String> config) throws IOException {
        validateConfig(config);

        String host = config.getOrDefault("host", "localhost");
        String port = config.getOrDefault("port", "5432");
        String user = config.getOrDefault("user", "postgres");
        String password = config.getOrDefault("password", "postgres");
        this.database = config.getOrDefault("database", "receiver");
        this.table = config.getOrDefault("table", "points");

        String sslmode = config.getOrDefault("sslmode", "disable");

        String connStr = String.format(
                "jdbc:postgresql://%s:%s/%s?user=%s&password=%s&ssl=%s",
                host, port, database, user, password, sslmode.equals("require") ? "true" : "false"
        );

        try {
            this.connection = DriverManager.getConnection(connStr);
            configureConnection();
            createTableIfNotExists();
            System.out.println("Подключились к PostgreSQL");
        } catch (SQLException e) {
            throw new IOException("Ошибка подключения к PostgreSQL: " + e.getMessage(), e);
        }
    }

    private void validateConfig(Map<String, String> config) {
        if (config == null || config.isEmpty()) {
            throw new IllegalArgumentException("Конфигурация не может быть null или пустой");
        }

        String[] requiredParams = {"host", "port", "user", "password", "database", "table", "sslmode"};
        for (String param : requiredParams) {
            if (!config.containsKey(param)) {
                throw new IllegalArgumentException("Отсутствует обязательный параметр: " + param);
            }
        }
    }

    private void configureConnection() {
        try {
            connection.setAutoCommit(true);
            connection.setNetworkTimeout(null, 5000);
        } catch (SQLException e) {
            System.err.println("Ошибка настройки соединения с PostgreSQL: " + e.getMessage());
        }
    }

    private void createTableIfNotExists() throws SQLException {
        String createTableSQL = String.format(
                "CREATE TABLE IF NOT EXISTS %s (id SERIAL PRIMARY KEY, point TEXT NOT NULL, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);",
                table
        );
        String createIndexSQL = String.format(
                "CREATE INDEX IF NOT EXISTS idx_%s_point ON %s (point);",
                table, table
        );

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createTableSQL);
            stmt.execute(createIndexSQL);
            System.out.println("Таблица " + table + " проверена/создана");
        }
    }

    @Override
    public void init(Map<String, String> config) throws IOException {}

    @Override
    public void save(Serializable data) throws IOException {
        String insertQuery = String.format("INSERT INTO %s (point) VALUES (?::jsonb)", table);

        try (PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
            byte[] jsonData = data.toBytes();

            if (jsonData == null || jsonData.length == 0) {
                throw new IOException("Данные для сохранения пустые");
            }

            String jsonString = new String(jsonData, StandardCharsets.UTF_8);
            System.out.println("JSON для отправки в БД: " + jsonString);

            stmt.setString(1, jsonString);

            stmt.executeUpdate();
        } catch (Exception e) {
            throw new IOException("Не удалось вставить запись в PostgreSQL: " + e.getMessage(), e);
        }
    }

    @Override
    public void close() throws IOException {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new IOException("Ошибка закрытия соединения с PostgreSQL", e);
        }
    }
}