package ru.practicum.service;

import java.time.Duration;
import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import ru.practicum.dto.User;
import ru.practicum.dto.Order;
import ru.practicum.dto.Topics;
import ru.practicum.Application;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.containers.KafkaContainer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import static org.junit.jupiter.api.Assertions.*;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@Testcontainers
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MessageProcessorImplTest {
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

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }


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

        await()
                .atMost(Duration.ofSeconds(15))
                .pollInterval(Duration.ofSeconds(3))
                .untilAsserted(() -> {
                    kafkaTemplate.send(Topics.ORDERS_TOPIC, order.getId().toString(), order).get();
                });
    }

    @Test
    void listenPushMessage() {
        kafkaTemplate.send(Topics.ORDERS_TOPIC, order.getId().toString(), order);

        await()
                .atMost(Duration.ofSeconds(15))
                .pollInterval(Duration.ofSeconds(3))
                .untilAsserted(() -> {
                    kafkaTemplate.flush();
                    assertDoesNotThrow(() -> {
                        kafkaTemplate.flush();
                    });
                });
    }

    @Test
    void listenPullMessage() {
        kafkaTemplate.send(Topics.ORDERS_TOPIC, order.getId().toString(), order);
        kafkaTemplate.flush();

        messageProcessor.listenPullMessage();

        await()
                .atMost(Duration.ofSeconds(15))
                .pollInterval(Duration.ofSeconds(3))
                .untilAsserted(() -> {
                    assertDoesNotThrow(() -> {
                        kafkaTemplate.send(Topics.ORDERS_TOPIC, order.getId().toString(), order).get();
                    });
                });
    }
}