package ru.practicum;

import java.util.UUID;
import java.math.BigDecimal;
import ru.practicum.dto.User;
import ru.practicum.dto.Order;
import java.util.Properties;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import ru.practicum.loader.PropertiesLoader;
import ru.practicum.producer.ProducerFactory;
import ru.practicum.consumer.PullConsumerFactory;
import ru.practicum.consumer.PushConsumerFactory;

public class Main {
    public static void main(String[] args) {
        Properties config = PropertiesLoader.loadProperties();
        ProducerFactory producerFactory = new ProducerFactory("orders", config);
        PushConsumerFactory pushConsumerFactory = new PushConsumerFactory("orders", config);
        PullConsumerFactory pullConsumerFactory = new PullConsumerFactory("orders", config);

        var pushExecutor = Executors.newSingleThreadExecutor();
        var pullExecutor = Executors.newSingleThreadExecutor();
        pushExecutor.execute(pushConsumerFactory::receiveMessage);
        pullExecutor.execute(pullConsumerFactory::receiveMessage);

        User user = new User(UUID.randomUUID(), "Djon", "Doe", "djon@mail.ru");
        for (int i = 0; i < 200; i++) {
            Order order = new Order(UUID.randomUUID(), "product" + i, "category" + i, BigDecimal.valueOf(100 * i), 2 * i, LocalDateTime.now(), user);
            producerFactory.sendMessage(order);
        }
    }
}