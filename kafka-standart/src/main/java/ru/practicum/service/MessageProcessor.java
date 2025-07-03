package ru.practicum.service;

import ru.practicum.dto.Order;

public interface MessageProcessor {
    void sendMessage(Order order);
    void listenPushMessage(Order order);
    void listenPullMessage();
}