package org.example.cli.receiver.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.Duration;
import java.util.Map;

@Getter
public class Settings {

    private String host;
    private String port;

    @JsonProperty("con_live_sec")
    private int connTtl;

    @JsonProperty("log_level")
    private String logLevel;

    private Map<String, Map<String, String>> storage;

    public String getListenAddress() {
        return host + ":" + port;
    }

    public Duration getEmptyConnTTL() {
        return Duration.ofSeconds(connTtl);
    }

    public LogLevel getParsedLogLevel() {
        return switch (logLevel.toUpperCase()) {
            case "DEBUG" -> LogLevel.DEBUG;
            case "INFO" -> LogLevel.INFO;
            case "WARN" -> LogLevel.WARN;
            case "ERROR" -> LogLevel.ERROR;
            default -> LogLevel.INFO;
        };
    }

    public enum LogLevel {
        DEBUG, INFO, WARN, ERROR
    }
}

