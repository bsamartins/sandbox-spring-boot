package com.github.bsamartins.springboot.notifications.controller

import com.github.bsamartins.springboot.notifications.domain.ChatCreate
import com.github.bsamartins.springboot.notifications.domain.File
import com.github.bsamartins.springboot.notifications.domain.persistence.Chat
import com.github.bsamartins.springboot.notifications.repository.ChatRepository
import com.github.bsamartins.springboot.notifications.test.ApplicationIntegrationTest
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.reactive.function.BodyInserters.fromObject

class ChatControllerTest: ApplicationIntegrationTest() {

    @Autowired
    private lateinit var webClient: WebTestClient

    @Autowired
    private lateinit var chatRepository: ChatRepository

    @BeforeEach
    fun setup() {
    }

    @Test
    fun create() {
        val file = File()
        file.content = "Hello World".toByteArray()
        file.name = "image.txt"
        file.mediaType = MediaType.valueOf("plain/text")

        val chatCreate = ChatCreate()
        chatCreate.name = "test chat"
        chatCreate.picture = file

        val result = webClient.post().uri("/api/chats")
                .body(fromObject(chatCreate))
                .headers{ headers -> headers.contentType = MediaType.APPLICATION_JSON }
                .headers(withUser())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectBody(Chat::class.java)
                .returnResult()
                .responseBody
        assertNotNull(result)
    }

    @Test
    fun chatJoin() {
        val chat = createAndSaveChat("test")

        webClient.post().uri("/api/chats/{id}/users", chat.id)
//                .body(empty())
                .headers{ headers -> headers.contentType = MediaType.APPLICATION_JSON }
                .headers(withUser())
                .exchange()
                .expectStatus().isCreated
    }

    private fun createAndSaveChat(name: String): Chat {
        val chat = Chat()
        chat.name = name
        chat.pictureId = ""

        return chatRepository.save(chat).block()
    }

    @Test
    fun chatJoin_alreadyInChat() {
        val user = getDefaultUser()
        val chat = createAndSaveChat("test")

        chatRepository.addUser(chat.id, user.id).block()

        webClient.post().uri("/api/chats/{id}/users", chat.id)
//                .body(empty())
                .headers{ headers -> headers.contentType = MediaType.APPLICATION_JSON }
                .headers(withUser(user))
                .exchange()
                .expectStatus().isNotModified
    }

    @Test
    fun chatJoin_chatNotFound() {
        webClient.post().uri("/api/chats/{id}/users", "123")
//                .body(empty())
                .headers{ headers -> headers.contentType = MediaType.APPLICATION_JSON }
                .headers(withUser())
                .exchange()
                .expectStatus().isNotFound
    }

    @Test
    fun chatLeave() {
        val user = getDefaultUser()
        val chat = createAndSaveChat("test")

        chatRepository.addUser(chat.id, user.id).block()

        webClient.delete().uri("/api/chats/{id}/users", chat.id)
                .headers(withUser(user))
                .exchange()
                .expectStatus().isOk
    }

    @Test
    fun chatLeave_alreadyLeftChat() {
        val user = getDefaultUser()
        val chat = createAndSaveChat("test")

        chatRepository.addUser(chat.id, user.id).block()
        chatRepository.removeUser(chat.id, user.id).block()

        webClient.delete().uri("/api/chats/{id}/users", chat.id)
                .headers(withUser(user))
                .exchange()
                .expectStatus().isNotModified
    }

    @Test
    fun chatLeave_neverJoinedChat() {
        val chat = createAndSaveChat("test")
        webClient.delete().uri("/api/chats/{id}/users", chat.id)
                .headers(withUser())
                .exchange()
                .expectStatus().isNotModified
    }

    @Test
    fun chatLeave_notFound() {
        webClient.delete().uri("/api/chats/{id}/users", "123")
                .headers(withUser())
                .exchange()
                .expectStatus().isNotFound
    }

    @Test
    fun findByQuery() {
        createAndSaveChat("super dupa chat")
        createAndSaveChat("regular chat")
        createAndSaveChat("123")

        webClient.get().uri("/api/chats?query={query}", "chat")
                .headers(withUser())
                .exchange()
                .expectStatus().isOk
                .expectBodyList(Chat::class.java).hasSize(2)

        webClient.get().uri("/api/chats?query={query}", "sup   cha")
                .headers(withUser())
                .exchange()
                .expectStatus().isOk
                .expectBodyList(Chat::class.java).hasSize(1)

        webClient.get().uri("/api/chats?query={query}", "ChAt")
                .headers(withUser())
                .exchange()
                .expectStatus().isOk
                .expectBodyList(Chat::class.java).hasSize(2)
    }
}
