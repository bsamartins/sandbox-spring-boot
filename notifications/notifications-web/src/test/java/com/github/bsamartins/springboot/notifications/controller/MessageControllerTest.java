package com.github.bsamartins.springboot.notifications.controller;

import com.github.bsamartins.springboot.notifications.ApplicationIntegrationTest;
import com.github.bsamartins.springboot.notifications.domain.persistence.Message;
import com.github.bsamartins.springboot.notifications.domain.persistence.User;
import com.github.bsamartins.springboot.notifications.messaging.RedisMessagePublisher;
import com.github.bsamartins.springboot.notifications.repository.MessageRepository;
import com.github.bsamartins.springboot.notifications.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MessageControllerTest extends ApplicationIntegrationTest {

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @Autowired
    private RedisMessagePublisher<com.github.bsamartins.springboot.notifications.domain.persistence.Message> messagePublisher;

    private User user;

    @BeforeEach
    public void setup() {
        user = new User();
        user.setUsername("test.user");
        user.setPassword("pwd");
        userRepository.save(user).block();
    }

    @Test
    public void getMessages() {
        Message message = new Message();
        message.setText("Hello World");
        message.setTimestamp(LocalDateTime.now());
        message.setUserId(user.getId());
        messageRepository.save(message).block();

        List<Message> messages = webClient.get().uri("/api/messages")
                .headers(withUser())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Message.class)
                .hasSize(1)
                .returnResult()
                .getResponseBody();
        assertEquals(true, messages.stream().allMatch(m -> message.getId().equals(m.getId())));
    }

    @Test
    public void streamMessages() {
        Message message = new Message();
        message.setText("Hello World");
        message.setTimestamp(LocalDateTime.now());
        message.setUserId(user.getId());
        messageRepository.save(message).block();

        Mono.delay(Duration.ofSeconds(1)).subscribe(t -> {
            messagePublisher.publish(message);
        });

        FluxExchangeResult<Message> result = webClient.get().uri("/api/messages/stream")
                .headers(withUser())
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Message.class);

        StepVerifier.create(result.getResponseBody())
                .expectNextCount(1)
                .thenCancel()
                .verify();
    }

    @AfterEach
    public void tearDown() {
        Mono.from(this.reactiveMongoTemplate.getMongoDatabase().drop())
                .subscribe();
    }
}
