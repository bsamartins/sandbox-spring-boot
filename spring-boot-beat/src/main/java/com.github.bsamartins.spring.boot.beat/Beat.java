package com.github.bsamartins.spring.boot.beat;

import java.time.OffsetDateTime;

public class Beat<T> {
    private OffsetDateTime timestamp;
    private T data;

    public Beat(T data) {
        this.timestamp = OffsetDateTime.now();
        this.data = data;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public T getData() {
        return data;
    }
}
