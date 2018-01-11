package com.github.bsamartins.springboot.notifications;

import org.junit.Test;
import reactor.core.publisher.Mono;

import static org.junit.Assert.assertEquals;

public class WebFluxTests {
    @Test
    public void test1() {
        Mono<String> monoWithString = testConsumer(Mono.just("s"));
        assertEquals("prefix_s", monoWithString.block());

        Mono<String> monoEmpty = testConsumer(Mono.empty());
        assertEquals("Hello World", monoEmpty.block());
    }

    private static Mono<String> testConsumer(Mono<String> mono) {
        return mono.map(e -> "prefix_" + e)
                .doOnNext(e -> System.out.println("doOnNext-First: " + e))
                .switchIfEmpty(Mono.just("Hello World"))
                .doOnNext(e -> System.out.println("doOnNext-Second: " + e));
    }

}
