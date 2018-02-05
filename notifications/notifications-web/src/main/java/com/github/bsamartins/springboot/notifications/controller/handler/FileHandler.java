package com.github.bsamartins.springboot.notifications.controller.handler;

import com.github.bsamartins.springboot.notifications.service.FileService;
import com.mongodb.reactivestreams.client.gridfs.AsyncInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.ContentDisposition;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static com.github.bsamartins.springboot.notifications.data.mongo.MongoHelper.toByteBuffer;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

@Component
public class FileHandler {

    @Autowired
    private FileService fileService;

    public Mono<ServerResponse> findById(ServerRequest request) {
        String id = request.pathVariable("id");
        DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
        return fileService.findById(id)
                .flatMap(f -> {
                    MediaType mediaType = Optional.ofNullable(f.getContentType())
                            .map(MediaType::valueOf)
                            .orElse(MediaType.APPLICATION_OCTET_STREAM);

                    AsyncInputStream stream = f.getAsyncInputStream();
                    return ServerResponse.ok()
                            .contentType(mediaType)
                            .contentLength(f.getContentLength())
                            .header(CONTENT_DISPOSITION, ContentDisposition.parse(
                                    String.format("attachment: filename\"%s\"", f.getFilename()))
                                    .toString())
                            .body(BodyInserters.fromDataBuffers(toByteBuffer(stream, dataBufferFactory)));
                })
                .log()
                .switchIfEmpty(ServerResponse.notFound().build());
    }
}
