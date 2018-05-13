package com.github.bsamartins.springboot.notifications.repository

import com.github.bsamartins.springboot.notifications.domain.persistence.Chat
import com.github.bsamartins.springboot.notifications.domain.persistence.User
import com.github.bsamartins.springboot.notifications.test.MongoIntegrationTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories
import org.springframework.test.context.junit.jupiter.SpringExtension
import reactor.test.StepVerifier

import java.util.UUID

import com.github.bsamartins.springboot.notifications.domain.persistence.ChatEvent.Type.USER_JOINED
import com.github.bsamartins.springboot.notifications.domain.persistence.ChatEvent.Type.USER_LEFT
import org.junit.jupiter.api.Assertions.assertEquals

@ExtendWith(SpringExtension::class)
@EnableReactiveMongoRepositories
class ChatRepositoryTest: MongoIntegrationTest() {

    @Autowired
    private lateinit var chatRepository: ChatRepository

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun findLastChatEventForUser() {

        val chat = Chat()
        chat.pictureId = UUID.randomUUID().toString()
        chat.name = "test"

        chatRepository.save(chat).block()

        val u1 = User()
        u1.username = "u1"
        u1.password = ""

        val u2 = User()
        u2.username = "u2"
        u2.password = ""

        userRepository.save(u1).block()
        userRepository.save(u2).block()

        chatRepository.addUser(chat.id, u1.id).block()
        chatRepository.removeUser(chat.id, u1.id).block()
        chatRepository.addUser(chat.id, u2.id).block()

        StepVerifier.create(chatRepository.findLastEventForUser(chat.id, u1.id, USER_LEFT))
                .assertNext{ c -> assertEquals(c.type, USER_LEFT) }
                .verifyComplete()
    }

    @Test
    fun findLastChatEventForUser_noResults() {

        val chat = Chat()
        chat.pictureId = UUID.randomUUID().toString()
        chat.name = "test"

        chatRepository.save(chat).block()

        StepVerifier.create(chatRepository.findLastEventForUser(chat.id, "u1", USER_LEFT))
                .expectNextCount(0)
                .expectComplete()
                .verify()
    }

    @Test
    fun findUsers() {
        val chat1 = createAndSaveChat("c1")
        val chat2 = createAndSaveChat("c2")

        val user1 = createAndSaveUser("u1")
        val user2 = createAndSaveUser("u2")
        val user3 = createAndSaveUser("u3")

        chatRepository.addUser(chat1.id, user1.id).block()
        chatRepository.addUser(chat1.id, user2.id).block()
        chatRepository.addUser(chat2.id, user3.id).block()

        StepVerifier.create(chatRepository.findUsers(chat1.id))
                .expectNext(user1.id, user2.id)
                .verifyComplete()
    }

    @Test
    fun findByUser() {
        val chat1 = createAndSaveChat("c1")
        val chat2 = createAndSaveChat("c2")
        val chat3 = createAndSaveChat("c3")

        val user1 = createAndSaveUser("u1")
        val user2 = createAndSaveUser("u2")

        chatRepository.addUser(chat1.id, user1.id).block()
        chatRepository.addUser(chat2.id, user1.id).block()
        chatRepository.addUser(chat2.id, user2.id).block()
        chatRepository.addUser(chat3.id, user2.id).block()

        StepVerifier.create(chatRepository.findAllByUser(user1.id))
                .assertNext{ c -> assertEquals(chat1.id, c.id) }
                .assertNext{ c -> assertEquals(chat2.id, c.id) }
                .verifyComplete()
    }

    @Test
    fun findUsers_noUsers() {
        val chat1 = createAndSaveChat("c1")
        StepVerifier.create(chatRepository.findUsers(chat1.id))
                .expectComplete()
    }

    @Test
    fun addUser() {
        val chat1 = createAndSaveChat("c1")
        val user1 = createAndSaveUser("u1")

        StepVerifier.create(chatRepository.addUser(chat1.id, user1.id))
            .verifyComplete()

        StepVerifier.create(chatRepository.findUsers(chat1.id))
                .expectNext(user1.id)
                .verifyComplete()

        StepVerifier.create(chatRepository.findLastEventForUser(chat1.id, user1.id))
                .assertNext{ e -> 
                    assertEquals(user1.id, e.userId)
                    assertEquals(USER_JOINED, e.type)
                }.verifyComplete()
    }

    @Test
    fun removeUser() {
        val chat1 = createAndSaveChat("c1")
        val user1 = createAndSaveUser("u1")

        StepVerifier.create(chatRepository.removeUser(chat1.id, user1.id))
                .verifyComplete()

        StepVerifier.create(chatRepository.findUsers(chat1.id))
                .verifyComplete()

        StepVerifier.create(chatRepository.findLastEventForUser(chat1.id, user1.id))
            .assertNext{ e ->
                assertEquals(user1.id, e.userId)
                assertEquals(USER_LEFT, e.type)
            }.verifyComplete()
    }

    @Test
    fun isUserInChat() {
        val chat1 = createAndSaveChat("c1")
        val user1 = createAndSaveUser("u1")

        StepVerifier.create(chatRepository.addUser(chat1.id, user1.id))
                .verifyComplete()

        StepVerifier.create(chatRepository.isUserInChat(chat1.id, user1.id))
                .expectNext(true)
                .verifyComplete()
    }

    @Test
    fun isUserInChat_not() {
        val chat1 = createAndSaveChat("c1")
        StepVerifier.create(chatRepository.isUserInChat(chat1.id, "x"))
                .expectNext(false)
                .verifyComplete()
    }

    @Test
    fun findByNameContaining() {
        createAndSaveChat("super dupa chat")
        createAndSaveChat("regular chat")
        StepVerifier.create(chatRepository.findByNameContaining("chat", null))
                .expectNextCount(2)
                .verifyComplete()

        StepVerifier.create(chatRepository.findByNameContaining("sup   cha", null))
                .expectNextCount(1)
                .verifyComplete()

        StepVerifier.create(chatRepository.findByNameContaining("ChAt", null))
                .expectNextCount(2)
                .verifyComplete()
    }

    private fun createAndSaveUser(username: String): User {
        val user = User()
        user.username = username
        user.password = ""
        return userRepository.save(user).block()
    }

    private fun createAndSaveChat(name: String): Chat {
        val chat = Chat()
        chat.name = name
        chat.pictureId = UUID.randomUUID().toString()
        return chatRepository.save(chat).block()
    }


}
