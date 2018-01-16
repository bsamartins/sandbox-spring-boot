package com.github.bsamartins.springboot.notifications.messaging;

public interface MessagePublisher<T> {
    void publish(T message);
}