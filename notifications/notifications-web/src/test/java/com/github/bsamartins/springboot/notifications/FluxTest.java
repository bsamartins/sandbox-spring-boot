package com.github.bsamartins.springboot.notifications;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.Duration;

public class FluxTest {

    @Test
    public void rangeCustom() {
        rangeCustom(1, 10).subscribe(System.out::println);
    }

    @Test
    public void zipEmpty() {
        Tuple2<Object, String> res = Mono.zip(Mono.empty(), Mono.just("abc")).block(Duration.ofSeconds(1));
    }

    private static Flux<Integer> rangeCustom(int start, int count) {
        return Flux.generate(() -> 0, (counter, s) -> {
            if(counter < count) {
                s.next(start + counter);
            } else {
                s.complete();
            }
            return counter + 1;
        });
    }

    @Test
    public void zip() {
        Mono.zip(Mono.just("s"), Mono.just("Abc"))
                .map(t -> {
                    t.getT1();
                    t.getT2();
                    return null;
                });

    }
}
