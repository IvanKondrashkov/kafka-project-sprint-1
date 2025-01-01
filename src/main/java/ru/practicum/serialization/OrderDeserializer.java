package ru.practicum.serialization;

import lombok.extern.slf4j.Slf4j;
import java.util.Map;
import ru.practicum.dto.Order;
import java.nio.charset.StandardCharsets;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.common.errors.SerializationException;

@Slf4j
public class OrderDeserializer implements Deserializer<Order> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        Deserializer.super.configure(configs, isKey);
    }

    @Override
    public Order deserialize(String topic, byte[] data) {
        objectMapper.registerModule(new JavaTimeModule());
        try {
            if (data == null){
                return null;
            }
            return objectMapper.readValue(new String(data, StandardCharsets.UTF_8), Order.class);
        } catch (Exception e) {
            throw new SerializationException("Error deserializing order!");
        }
    }

    @Override
    public void close() {
        Deserializer.super.close();
    }
}