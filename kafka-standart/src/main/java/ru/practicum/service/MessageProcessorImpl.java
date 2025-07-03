package ru.practicum.service;

import java.util.UUID;
import java.time.Duration;
import java.util.Collections;
import ru.practicum.dto.Order;
import ru.practicum.dto.Topics;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageProcessorImpl implements MessageProcessor {
    private final KafkaTemplate<String, Order> kafkaTemplate;
    private final KafkaConsumer<String, Order> consumer;

    @Override
    public void sendMessage(Order order) {
        log.info("send order={}", order);
        kafkaTemplate.send(Topics.ORDERS_TOPIC, UUID.randomUUID().toString(), order);
    }

    @KafkaListener(topics = Topics.ORDERS_TOPIC, groupId = "orders-group-push", containerFactory = "orderPushKafkaListenerContainerFactory")
    public void listenPushMessage(Order order) {
        log.info("receive push order={}", order);
    }

    @Scheduled(fixedRate = 10000)
    public void listenPullMessage() {
        receiveMessage();
    }

    private void receiveMessage() {
        consumer.subscribe(Collections.singletonList(Topics.ORDERS_TOPIC));

        ConsumerRecords<String, Order> records = consumer.poll(Duration.ofMillis(10000));
        records.forEach(record -> {
            log.info("receive pull order: key={}, value={}, partition={}, offset={}",
                    record.key(),
                    record.value(),
                    record.partition(),
                    record.offset()
            );
        });

        if (!records.isEmpty()) {
            consumer.commitSync();
        }
    }
}