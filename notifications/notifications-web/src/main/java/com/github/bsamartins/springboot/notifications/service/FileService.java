package com.github.bsamartins.springboot.notifications.service;

import com.mongodb.reactivestreams.client.gridfs.AsyncInputStream;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsCriteria;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import pt.bsamartins.spring.data.mongo.gridfs.ReactiveGridFsResource;
import pt.bsamartins.spring.data.mongo.gridfs.ReactiveGridFsTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.springframework.data.mongodb.core.query.Query.query;

@Service
public class FileService {

    @Autowired
    private ReactiveGridFsTemplate reactiveGridFsTemplate;

    public Mono<ReactiveGridFsResource> findById(String id) {
        return reactiveGridFsTemplate.findOne(query(new GridFsCriteria("_id").is(id)))
                .log()
                .flatMap(file -> reactiveGridFsTemplate.getResource(file.getFilename()))
                .log();
    }

    public Mono<ReactiveGridFsResource> findByLocation(String location) {
        return reactiveGridFsTemplate.getResource(location);
    }

    public Flux<ReactiveGridFsResource> findAll() {
        return reactiveGridFsTemplate.getResources("*");
    }

    public Mono<String> store(AsyncInputStream asyncInputStream, String name, MediaType mediaType) {
        String contentType = Optional.ofNullable(mediaType)
                .map(MediaType::toString)
                .orElse(null);
        return reactiveGridFsTemplate.store(asyncInputStream, name, contentType)
                .map(ObjectId::toString);
    }
}
