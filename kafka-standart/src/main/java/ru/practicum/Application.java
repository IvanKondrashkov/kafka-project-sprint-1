package ru.practicum;

import java.util.UUID;
import java.util.Random;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import ru.practicum.dto.User;
import ru.practicum.dto.Order;
import ru.practicum.dto.Topics;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.kafka.core.KafkaTemplate;

@EnableScheduling
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(KafkaTemplate<String, Order> kafkaTemplate) {
        return args -> {
            for (int i = 0; i < 100000; i++) {
                var order = Order.builder()
                        .id(UUID.randomUUID())
                        .name("name" + i)
                        .category("category" + i)
                        .amount(BigDecimal.valueOf(new Random().nextLong()))
                        .quantity(new Random().nextInt())
                        .dateCreated(LocalDateTime.now())
                        .user(new User())
                        .build();
                kafkaTemplate.send(Topics.ORDERS_TOPIC, UUID.randomUUID().toString(), order);
            }
        };
    }
}