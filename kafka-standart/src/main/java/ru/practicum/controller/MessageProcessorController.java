package ru.practicum.controller;

import ru.practicum.dto.Order;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import ru.practicum.service.MessageProcessor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class MessageProcessorController {
    private final MessageProcessor messageProcessor;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void sendMessage(@RequestBody Order order) {
        log.info("send request /orders {}", order);
        messageProcessor.sendMessage(order);
    }
}