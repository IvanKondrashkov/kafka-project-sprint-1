package ru.practicum.serialization;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.dto.Order;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.common.errors.SerializationException;

@Slf4j
public class OrderSerializer implements Serializer<Order> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(String topic, Order data) {
        objectMapper.registerModule(new JavaTimeModule());
        try {
            if (data == null) {
                return null;
            }
            log.info("Serializing order={}", data);
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception ex) {
            throw new SerializationException("Error serializing order!");
        }
    }

    @Override
    public void close() {
        Serializer.super.close();
    }
}