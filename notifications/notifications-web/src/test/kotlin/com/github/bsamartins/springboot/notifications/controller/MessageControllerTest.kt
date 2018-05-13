package com.github.bsamartins.springboot.notifications.controller

import com.github.bsamartins.springboot.notifications.domain.persistence.Message
import com.github.bsamartins.springboot.notifications.domain.persistence.User
import com.github.bsamartins.springboot.notifications.messaging.RedisMessagePublisher
import com.github.bsamartins.springboot.notifications.repository.MessageRepository
import com.github.bsamartins.springboot.notifications.repository.UserRepository
import com.github.bsamartins.springboot.notifications.test.ApplicationIntegrationTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.Duration
import java.time.LocalDateTime

class MessageControllerTest: ApplicationIntegrationTest() {

    @Autowired
    private lateinit var webClient: WebTestClient

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var messageRepository: MessageRepository

    @Autowired
    private lateinit var reactiveMongoTemplate: ReactiveMongoTemplate

    @Autowired
    private lateinit var messagePublisher: RedisMessagePublisher<com.github.bsamartins.springboot.notifications.domain.persistence.Message>

    private lateinit var user: User

    @BeforeEach
    fun setup() {
        user = User()
        user.username = "test.user"
        user.password = "pwd"
        userRepository.save(user).block()
    }

    @Test
    fun getMessages() {
        val message = Message()
        message.text = "Hello World"
        message.timestamp = LocalDateTime.now()
        message.userId = user.id
        messageRepository.save(message).block()

        val messages = webClient.get().uri("/api/messages")
                .headers(withUser())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBodyList(Message::class.java)
                .hasSize(1)
                .returnResult()
                .responseBody
        assertEquals(true, messages.stream().allMatch{ m -> message.id == m.id })
    }

    @Test
    fun streamMessages() {
        val message = Message()
        message.text = "Hello World"
        message.timestamp = LocalDateTime.now()
        message.userId = user.id
        messageRepository.save(message).block()

        Mono.delay(Duration.ofSeconds(1)).subscribe { _ -> messagePublisher.publish(message)}

        val result = webClient.get().uri("/api/messages/stream")
                .headers(withUser())
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk
                .returnResult(Message::class.java)

        StepVerifier.create(result.responseBody)
                .expectNextCount(1)
                .thenCancel()
                .verify()
    }

    @AfterEach
    fun tearDown() {
        Mono.from(this.reactiveMongoTemplate.mongoDatabase.drop())
                .subscribe()
    }
}
