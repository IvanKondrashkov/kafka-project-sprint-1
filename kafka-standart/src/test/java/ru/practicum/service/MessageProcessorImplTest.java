package ru.practicum.service;

import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import ru.practicum.dto.User;
import ru.practicum.dto.Order;
import ru.practicum.Application;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.containers.KafkaContainer;
import org.springframework.kafka.core.KafkaTemplate;
import static org.junit.jupiter.api.Assertions.*;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@Testcontainers
@TestPropertySource("classpath:application-test.properties")
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MessageProcessorImplTest {
    @Value(value = "${kafka.topic.name}")
    private String topic;
    @Autowired
    private MessageProcessor messageProcessor;
    @Autowired
    private KafkaTemplate<String, Order> kafkaTemplate;
    private Order order;

    @Container
    @ServiceConnection
    public static KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.4.0")
    ).withKraft();


    @BeforeEach
    void setUp() {
        order = Order.builder()
                .id(UUID.randomUUID())
                .name("Smartphone")
                .category("Electronics")
                .amount(BigDecimal.valueOf(999.99))
                .quantity(1)
                .dateCreated(LocalDateTime.now())
                .user(User.builder()
                        .id(UUID.randomUUID())
                        .firstName("John")
                        .lastName("Doe")
                        .email("john.doe@mail.ru")
                        .build())
                .build();
    }

    @AfterEach
    void tearDown() {
        order = null;
    }

    @Test
    void sendMessage() {
        messageProcessor.sendMessage(order);

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            assertDoesNotThrow(() -> {
                kafkaTemplate.send(topic, order.getId().toString(), order).get();
            });
        });
    }

    @Test
    void listenPushMessage() {
        kafkaTemplate.send(topic, order.getId().toString(), order);

        await().atMost(15, TimeUnit.SECONDS).untilAsserted(() -> {
            assertDoesNotThrow(() -> {
                kafkaTemplate.flush();
            });
        });
    }

    @Test
    void listenPullMessage() {
        kafkaTemplate.send(topic, order.getId().toString(), order);
        kafkaTemplate.flush();

        messageProcessor.listenPullMessage();

        await().atMost(15, TimeUnit.SECONDS).untilAsserted(() -> {
            assertDoesNotThrow(() -> {
                kafkaTemplate.send(topic, order.getId().toString(), order).get();
            });
        });
    }
}