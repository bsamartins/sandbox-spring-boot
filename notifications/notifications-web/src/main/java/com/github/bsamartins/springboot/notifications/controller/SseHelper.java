package com.github.bsamartins.springboot.notifications.controller;

import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class SseHelper {

    public static <T> Flux<ServerSentEvent<T>> sse(Duration duration) {
        return Flux.interval(Duration.ofSeconds(5))
                .map(t -> ServerSentEvent
                        .<T>builder()
                        .comment("")
                        .build());
    }

    public static <T> Flux<ServerSentEvent<T>> sse() {
        return sse(Duration.ofSeconds(5));
    }
}
