package org.example;

import org.example.cli.receiver.config.ConfigLoader;
import org.example.cli.receiver.config.Settings;
import org.example.cli.receiver.storage.*;
import org.example.cli.receiver.server.EgtsServer;
import org.example.cli.receiver.server.LoggingSetup;

public class Main {
    public static void main(String[] args) {
        try {
            // Загрузка конфигурации
            Settings settings = ConfigLoader.load("D:\\Саня\\java\\EGTS\\untitled\\src\\main\\java\\org\\example\\configs\\receiver.yaml");

            // Настройка логирования
            LoggingSetup.setLevel(settings.getParsedLogLevel());

            // Инициализация хранилища
            Repository storage = new Repository();
            if (settings.getStorage() != null && !settings.getStorage().isEmpty()) {
                storage.loadStorages(settings.getStorage());
            }

            // Запуск сервера
            EgtsServer server = new EgtsServer(
                    settings.getHost(),
                    Integer.parseInt(settings.getPort()),
                    settings.getEmptyConnTTL(),
                    storage
            );

            server.run();

        } catch (Exception e) {
            System.err.println("Ошибка запуска сервера: " + e.getMessage());
            e.printStackTrace();
        }
    }
}