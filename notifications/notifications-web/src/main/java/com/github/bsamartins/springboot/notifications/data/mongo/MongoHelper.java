package com.github.bsamartins.springboot.notifications.data.mongo;

import com.mongodb.reactivestreams.client.gridfs.AsyncInputStream;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;

public class MongoHelper {

    public static Flux<DataBuffer> toByteBuffer(AsyncInputStream stream, DataBufferFactory dataBufferFactory) {
        return pushRead(stream, dataBufferFactory);
    }

    private static Flux<DataBuffer> pushRead(AsyncInputStream stream, DataBufferFactory dataBufferFactory) {
        return Flux.push(emitter -> {
            read(stream, dataBufferFactory, emitter);
        });
    }

    private static void read(AsyncInputStream stream, DataBufferFactory dataBufferFactory, FluxSink<DataBuffer> sink) {
        if(sink.isCancelled()) {
            return;
        }
        ByteBuffer byteBuffer = ByteBuffer.allocate(40000);
        Mono.from(stream.read(byteBuffer)).subscribe(t -> {
            if(t >= 0) {
                byteBuffer.flip();
                sink.next(dataBufferFactory.wrap(byteBuffer));
                read(stream, dataBufferFactory, sink);
            } else {
                sink.complete();
            }
        }, sink::error, sink::complete);
    }

}
