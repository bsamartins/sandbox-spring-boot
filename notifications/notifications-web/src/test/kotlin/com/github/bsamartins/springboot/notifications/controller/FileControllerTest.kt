package com.github.bsamartins.springboot.notifications.controller;

import com.github.bsamartins.springboot.notifications.InputStreamUtils
import com.github.bsamartins.springboot.notifications.domain.persistence.Message
import com.github.bsamartins.springboot.notifications.test.ApplicationIntegrationTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import pt.bsamartins.spring.data.mongo.gridfs.ReactiveGridFsTemplate
import java.io.ByteArrayInputStream

class FileControllerTest: ApplicationIntegrationTest() {

    @Autowired
    private lateinit var webClient: WebTestClient

    @Autowired
    private lateinit var reactiveGridFsTemplate: ReactiveGridFsTemplate

    @BeforeEach
    fun setup() {}

    @Test
    fun getFile() {
        val ais = InputStreamUtils.toAsyncInputStream(ByteArrayInputStream("Hello World".toByteArray()))
//        MongoHelper.toByteBuffer(ais).subscribe(b -> {
//            System.out.println(b);
//            System.out.println(new String(b.));
//        });
        val id = reactiveGridFsTemplate.store(ais, "test.txt")
                .block()
        webClient.get().uri("/api/files/{id}", id)
                .headers(withUser())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBodyList(Message::class.java)
                .hasSize(1)
    }

    @AfterEach
    fun tearDown() {
//        Mono.from(this.reactiveMongoTemplate.getMongoDatabase().drop())
//                .subscribe();
    }
}
