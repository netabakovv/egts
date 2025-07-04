package org.example.cli.reciever.server;

import org.example.cli.receiver.storage.Repository;
import org.example.libs.EgtsPackage;
import org.example.libs.RecordDataSet;
import org.example.libs.ServiceDataSet;
import org.example.cli.receiver.storage.NavRecord;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EgtsServer {
    private static final Logger logger = LoggerFactory.getLogger(EgtsServer.class);
    private final String address;
    private final int port;
    private final Duration ttl;
    private final Repository storage;

    public EgtsServer(String address, int port, Duration ttl, Repository storage) {
        this.address = address;
        this.port = port;
        this.ttl = ttl;
        this.storage = storage;
    }

    public void run() throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        logger.info("Сервер запущен на {}:{}", address, port);

        ExecutorService executor = Executors.newCachedThreadPool();

        while (!serverSocket.isClosed()) {
            try {
                Socket clientSocket = serverSocket.accept();
                logger.info("Установлено соединение с {}", clientSocket.getRemoteSocketAddress());
                executor.execute(new ConnectionHandler(clientSocket, storage));
            } catch (IOException e) {
                logger.error("Ошибка при подключении клиента", e);
            }
        }

        executor.shutdownNow();
    }
}