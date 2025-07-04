package org.example;

import org.example.cli.receiver.storage.Repository;
import org.example.cli.reciever.server.EgtsServer;
import org.example.libs.StorageRecordWrapper;

import java.time.Duration;

public class Main {
    public static void main(String[] args) throws Exception {
        Repository storage = new Repository();
        EgtsServer server = new EgtsServer("0.0.0.0", 8080, Duration.ofSeconds(30), storage);
        server.run();
    }
}