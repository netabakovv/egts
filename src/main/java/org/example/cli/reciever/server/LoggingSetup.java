package org.example.cli.reciever.server;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;

public class LoggingSetup {
    public static void setLevel(org.example.cli.receiver.config.Settings.LogLevel level) {
        Level logLevel = switch (level) {
            case DEBUG -> Level.DEBUG;
            case WARN -> Level.WARN;
            case ERROR -> Level.ERROR;
            default -> Level.INFO;
        };
        ((Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME)).setLevel(logLevel);
    }
}
