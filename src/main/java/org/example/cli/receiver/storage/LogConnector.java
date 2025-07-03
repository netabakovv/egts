package org.example.cli.receiver.storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class LogConnector {

    private static final Logger logger = LoggerFactory.getLogger(LogConnector.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Имитация инициализации (всегда успешна)
     */
    public void init(Map<String, String> config) {}

    /**
     * Сохраняет сообщение, сериализуя его в красивый JSON и логируя
     */
    public void save(Serializable msg) {
        try {
            byte[] jsonBytes = msg.toBytes();
            String jsonStr = new String(jsonBytes);
            logger.info("Export packet:\n{}", jsonStr);
        } catch (JsonProcessingException e) {
            logger.error("Ошибка сериализации пакета", e);
        }
    }

    /**
     * Закрывает ресурсы (ничего не делает)
     */
    public void close() {}
}
