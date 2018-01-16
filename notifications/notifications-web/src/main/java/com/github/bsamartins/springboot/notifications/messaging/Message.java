package com.github.bsamartins.springboot.notifications.messaging;

import java.util.HashMap;
import java.util.Map;

public class Message<T> {
    private T payload;
    private Map<String, Object> headers = new HashMap<>();

    public Message(T payload) {
        this.payload = payload;
    }

    public T getPayload() {
        return payload;
    }

    public Map<String, Object> getHeaders() {
        return headers;
    }
}
