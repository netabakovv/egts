package org.example.cli.receiver.storage;

import org.example.cli.receiver.storage.store.clickhous.ClickHouseConnector;
import org.example.cli.receiver.storage.store.postgresql.PGConnector;
import org.example.cli.receiver.storage.store.rabbitmq.RabbitMQConnector;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class Repository {
    private final List<Saver<Serializable>> storages = new ArrayList<>();

    public void addStore(Saver<Serializable> store) {
        storages.add(store);
    }

    public void save(NavRecord message) throws IOException {
        for (Saver<Serializable> store : storages) {
            store.save(message);
        }
    }

    public void loadStorages(Map<String, Map<String, String>> storagesConfig) throws IOException, SQLException {
        if (storagesConfig == null || storagesConfig.isEmpty()) {
            throw new IOException("Настройки хранилищ отсутствуют");
        }

        for (Map.Entry<String, Map<String, String>> entry : storagesConfig.entrySet()) {
            String storeName = entry.getKey();
            Map<String, String> params = entry.getValue();

            Store<Serializable> store = switch (storeName.toLowerCase()) {
                case "rabbitmq" -> new RabbitMQConnector(params);
                case "clickhouse" -> new ClickHouseConnector(params);
                case "postgresql" -> new PGConnector(params);
                default -> throw new IOException("Хранилище не поддерживается: " + storeName);
            };

            addStore(store);
        }
    }
}
