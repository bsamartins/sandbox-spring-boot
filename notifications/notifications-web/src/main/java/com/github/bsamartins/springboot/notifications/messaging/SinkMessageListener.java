package com.github.bsamartins.springboot.notifications.messaging;

import reactor.core.publisher.FluxSink;

public class SinkMessageListener implements org.springframework.data.redis.connection.MessageListener {

    public FluxSink<org.springframework.data.redis.connection.Message> sink;

    public SinkMessageListener(FluxSink<org.springframework.data.redis.connection.Message> sink) {
        this.sink = sink;
    }

    @Override
    public void onMessage(org.springframework.data.redis.connection.Message message, byte[] pattern) {
        sink.next(message);
    }
}
