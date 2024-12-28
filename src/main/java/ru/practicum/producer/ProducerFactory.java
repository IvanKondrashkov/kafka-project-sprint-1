package ru.practicum.producer;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import java.util.Properties;
import ru.practicum.dto.Order;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.config.TopicConfig;
import ru.practicum.serialization.OrderSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

@Data
@Slf4j
@RequiredArgsConstructor
public class ProducerFactory {
    private final String targetTopic;
    private final Properties config;

    private Properties producerConfig() {
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.getProperty("bootstrap.servers"));
        properties.put(ProducerConfig.ACKS_CONFIG, config.getProperty("producer.acks"));
        properties.put(ProducerConfig.RETRIES_CONFIG, config.getProperty("producer.retries"));
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, OrderSerializer.class.getName());
        properties.put(TopicConfig.MIN_IN_SYNC_REPLICAS_CONFIG, config.getProperty("producer.min.insync.replicas"));
        return properties;
    }

    private KafkaProducer<String, Order> producerFactory() {
        return new KafkaProducer<>(producerConfig());
    }

    public void sendMessage(Order order) {
        try (var producer = producerFactory()) {
            ProducerRecord<String, Order> record = new ProducerRecord<>(targetTopic, order);
            log.info("Send message: key = {}, value = {}, partition = {}", record.key(), record.value(), record.partition());
            producer.send(record);
        } catch (Exception ex) {
            log.error("Error send kafka request", ex);
        }
    }
}