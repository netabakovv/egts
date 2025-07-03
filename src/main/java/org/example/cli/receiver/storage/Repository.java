package org.example.cli.receiver.storage;

import org.example.cli.receiver.storage.store.clickhous.ClickHouseConnector;
import org.example.cli.receiver.storage.store.rabbitmq.RabbitMQConnector;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class Repository<T extends Serializable> {
    private final List<Saver<T>> storages = new ArrayList<>();

    public void addStore(Saver<T> store) {
        storages.add(store);
    }

    public void save(T message) throws IOException {
        for (Saver<T> store : storages) {
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

            Store<T> store = switch (storeName.toLowerCase()) {
                case "rabbitmq" -> (Store<T>) new RabbitMQConnector(params);
                case "clickhouse" -> (Store<T>) new ClickHouseConnector(params);
                default -> throw new IOException("Хранилище не поддерживается: " + storeName);
            };

            addStore(store);
        }
    }
}
