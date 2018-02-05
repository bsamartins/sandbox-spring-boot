package com.github.bsamartins.springboot.notifications.controller;

import com.github.bsamartins.springboot.notifications.ApplicationIntegrationTest;
import com.github.bsamartins.springboot.notifications.InputStreamUtils;
import com.github.bsamartins.springboot.notifications.domain.persistence.Message;
import com.mongodb.reactivestreams.client.gridfs.AsyncInputStream;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import pt.bsamartins.spring.data.mongo.gridfs.ReactiveGridFsTemplate;

import java.io.ByteArrayInputStream;
import java.util.List;

public class FileControllerTest extends ApplicationIntegrationTest
{

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private ReactiveGridFsTemplate reactiveGridFsTemplate;

    @BeforeEach
    public void setup() {
    }

    @Test
    public void getFile() {
        AsyncInputStream ais = InputStreamUtils.toAsyncInputStream(new ByteArrayInputStream("Hello World".getBytes()));
//        MongoHelper.toByteBuffer(ais).subscribe(b -> {
//            System.out.println(b);
//            System.out.println(new String(b.));
//        });
        ObjectId id = reactiveGridFsTemplate.store(ais, "test.txt")
                .block();
        List<Message> files = webClient.get().uri("/api/files/{id}", id)
                .headers(withUser())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Message.class)
                .hasSize(1)
                .returnResult()
                .getResponseBody();
    }

    @AfterEach
    public void tearDown() {
//        Mono.from(this.reactiveMongoTemplate.getMongoDatabase().drop())
//                .subscribe();
    }
}
