package ru.practicum.config;

import java.util.Map;
import java.util.HashMap;
import ru.practicum.dto.Order;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import ru.practicum.serialization.OrderDeserializer;
import org.springframework.kafka.core.ConsumerFactory;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.apache.kafka.common.serialization.StringDeserializer;

@EnableKafka
@Configuration
public class KafkaPullConsumerConfig {
    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;
    @Value(value = "${pull.consumer.group.id}")
    private String groupId;
    @Value(value = "${pull.consumer.auto.offset.reset}")
    private String autoOffset;
    @Value(value = "${pull.consumer.enable.auto.commit}")
    private String enableAutoCommit;
    @Value(value = "${pull.consumer.max.poll.records}")
    private Integer maxPollRecords;
    @Value(value = "${pull.consumer.max.poll.records}")
    private Integer fetchMinBytes;

    @Bean
    public ConsumerFactory<String, Order> orderPullConsumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, OrderDeserializer.class.getName());
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffset);
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        config.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, fetchMinBytes);
        return new DefaultKafkaConsumerFactory<>(config);
    }

    @Bean
    public KafkaConsumer<String, Order> orderPullConsumer() {
        return (KafkaConsumer<String, Order>) orderPullConsumerFactory().createConsumer();
    }
}