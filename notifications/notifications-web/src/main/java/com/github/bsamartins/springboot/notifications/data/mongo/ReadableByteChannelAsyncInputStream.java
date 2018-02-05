package com.github.bsamartins.springboot.notifications.data.mongo;

import com.mongodb.reactivestreams.client.Success;
import com.mongodb.reactivestreams.client.gridfs.AsyncInputStream;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

public final class ReadableByteChannelAsyncInputStream implements AsyncInputStream {

        private ReadableByteChannel channel;

        public ReadableByteChannelAsyncInputStream(ReadableByteChannel channel) {
            this.channel = channel;
        }

        @Override
        public Publisher<Integer> read(ByteBuffer dst) {
            return Mono.create((s) -> {
                try {
                    int read = channel.read(dst);
                    s.success(read);
                } catch (IOException ex) {
                    s.error(ex);
                }

            });
        }

        @Override
        public Publisher<Success> close() {
            return Mono.create(s -> {
                try {
                    this.channel.close();
                    s.success(Success.SUCCESS);
                } catch (IOException e) {
                    s.error(e);
                }
            });
        }
    }