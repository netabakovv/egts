package org.example.cli.receiver.storage.store.rabbitmq;

import org.example.cli.receiver.storage.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

import java.io.IOException;
import java.util.Map;

public class RabbitMQConnector implements Store<Serializable> {

    private final CachingConnectionFactory connectionFactory;
    private final RabbitTemplate rabbitTemplate;

    private final String exchange;
    private final String routingKey;

    public RabbitMQConnector(Map<String, String> config) throws IOException {
        String host = config.getOrDefault("host", "localhost");
        int port = Integer.parseInt(config.getOrDefault("port", "5672"));
        String user = config.getOrDefault("user", "guest");
        String password = config.getOrDefault("password", "guest");
        this.exchange = config.getOrDefault("exchange", "");
        this.routingKey = config.getOrDefault("key", "");

        connectionFactory = new CachingConnectionFactory(host, port);
        connectionFactory.setUsername(user);
        connectionFactory.setPassword(password);

        rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
    }

    @Override
    public void init(Map<String, String> config) throws IOException {
        // Инициализация уже выполнена в конструкторе
    }

    @Override
    public void save(Serializable data) throws IOException {
        try {
            byte[] payload = data.toBytes();
            rabbitTemplate.convertAndSend(exchange, routingKey, payload);
        } catch (Exception e) {
            throw new IOException("Ошибка отправки сообщения в RabbitMQ", e);
        }
    }

    @Override
    public void close() throws IOException {
        if (connectionFactory != null) {
            connectionFactory.destroy();
        }
    }
}