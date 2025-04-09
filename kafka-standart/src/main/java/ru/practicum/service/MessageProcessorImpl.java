package ru.practicum.service;

import java.util.UUID;
import java.time.Duration;
import java.util.Collections;
import ru.practicum.dto.Order;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageProcessorImpl implements MessageProcessor {
    @Value(value = "${kafka.topic.name}")
    private String topic;
    private final KafkaTemplate<String, Order> kafkaTemplate;
    private final KafkaConsumer<String, Order> consumer;

    @Override
    public void sendMessage(Order order) {
        log.info("send order={}", order);
        kafkaTemplate.send(topic, UUID.randomUUID().toString(), order);
    }

    @KafkaListener(topics = "orders", groupId = "orders-group-push", containerFactory = "orderPushKafkaListenerContainerFactory")
    public void listenPushMessage(Order order) {
        log.info("receive push order={}", order);
    }

    @Scheduled(fixedRate = 10000)
    public void listenPullMessage() {
        receiveMessage();
    }

    private void receiveMessage() {
        consumer.subscribe(Collections.singletonList(topic));

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