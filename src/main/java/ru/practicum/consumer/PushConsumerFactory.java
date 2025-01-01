package ru.practicum.consumer;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import java.time.Duration;
import java.util.Properties;
import java.util.Collections;
import ru.practicum.dto.Order;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import ru.practicum.serialization.OrderDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

@Data
@Slf4j
@RequiredArgsConstructor
public class PushConsumerFactory {
    private final String targetTopic;
    private final Properties config;

    private Properties consumerConfig() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getProperty("bootstrap.servers"));
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, config.getProperty("push.consumer.group.id"));
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, OrderDeserializer.class.getName());
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, config.getProperty("push.consumer.auto.offset.reset"));
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, config.getProperty("push.consumer.enable.auto.commit"));
        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, config.getProperty("push.consumer.max.poll.records"));
        return properties;
    }

    private KafkaConsumer<String, Order> consumerFactory() {
        return new KafkaConsumer<>(consumerConfig());
    }

    public void receiveMessage() {
        try (var consumer = consumerFactory()) {
            consumer.subscribe(Collections.singletonList(targetTopic));
            while (true) {
                ConsumerRecords<String, Order> records = consumer.poll(Duration.ofMillis(1));
                records.forEach(record -> {
                    log.info("Receive message: key = {}, value = {}, partition = {}, offset = {}", record.key(), record.value(), record.partition(), record.offset());
                });
            }
        } catch (Exception ex) {
            log.error("Error receive kafka request", ex);
        }
    }
}