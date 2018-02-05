package com.github.bsamartins.springboot.notifications;

import com.mongodb.reactivestreams.client.Success;
import com.mongodb.reactivestreams.client.gridfs.AsyncInputStream;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class InputStreamUtils {

    public static AsyncInputStream toAsyncInputStream(InputStream inputStream) {
        return new AsyncInputStream() {
            @Override
            public Publisher<Integer> read(ByteBuffer dst) {
                return Mono.create(s -> {
                    try {
                        byte buffer[] = new byte[dst.remaining()];
                        int read = inputStream.read(buffer);

                        if(read >= 0) {
                            dst.put(buffer);
                            dst.position(read);
                            s.success(read);
                        } else {
                            s.success(-1);
                        }
                    } catch (IOException e) {
                        s.error(e);
                    }
                });
            }

            @Override
            public Publisher<Success> close() {
                return s -> {
                    try {
                        inputStream.close();
                        s.onNext(Success.SUCCESS);
                    } catch (IOException e) {
                        s.onError(e);
                    }
                };
            }
        };
    }
}
