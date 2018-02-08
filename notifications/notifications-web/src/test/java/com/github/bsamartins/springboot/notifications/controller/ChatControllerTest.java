package com.github.bsamartins.springboot.notifications.controller;

import com.github.bsamartins.springboot.notifications.domain.ChatCreate;
import com.github.bsamartins.springboot.notifications.domain.File;
import com.github.bsamartins.springboot.notifications.domain.persistence.Chat;
import com.github.bsamartins.springboot.notifications.domain.persistence.User;
import com.github.bsamartins.springboot.notifications.repository.ChatRepository;
import com.github.bsamartins.springboot.notifications.test.ApplicationIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.web.reactive.function.BodyInserters.empty;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;

class ChatControllerTest extends ApplicationIntegrationTest {

    @Autowired
    private WebTestClient webClient;

    @Autowired
    private ChatRepository chatRepository;

    @BeforeEach
    void setup() {
    }

    @Test
    void create() {
        File file = new File();
        file.setContent("Hello World".getBytes());
        file.setName("image.txt");
        file.setMediaType(MediaType.valueOf("plain/text"));

        ChatCreate chatCreate = new ChatCreate();
        chatCreate.setName("test chat");
        chatCreate.setPicture(file);

        Chat result = webClient.post().uri("/api/chats")
                .body(fromObject(chatCreate))
                .headers(headers -> headers.setContentType(MediaType.APPLICATION_JSON))
                .headers(withUser())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Chat.class)
                .returnResult()
                .getResponseBody();
        assertNotNull(result);
    }

    @Test
    void chatJoin() {
        Chat chat = createChat();

        webClient.post().uri("/api/chats/{id}/memberships", chat.getId())
                .body(empty())
                .headers(headers -> headers.setContentType(MediaType.APPLICATION_JSON))
                .headers(withUser())
                .exchange()
                .expectStatus().isCreated();
    }

    private Chat createChat() {
        Chat chat = new Chat();
        chat.setName("test");
        chat.setPictureId("");

        return chatRepository.save(chat).block();
    }

    @Test
    void chatJoin_alreadyInChat() {
        User user = getDefaultUser();
        Chat chat = createChat();

        chatRepository.addUser(chat.getId(), user.getId()).block();

        webClient.post().uri("/api/chats/{id}/memberships", chat.getId())
                .body(empty())
                .headers(headers -> headers.setContentType(MediaType.APPLICATION_JSON))
                .headers(withUser(user))
                .exchange()
                .expectStatus().isNotModified();
    }

    @Test
    void chatJoin_chatNotFound() {
        webClient.post().uri("/api/chats/{id}/memberships", "123")
                .body(empty())
                .headers(headers -> headers.setContentType(MediaType.APPLICATION_JSON))
                .headers(withUser())
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void chatLeave() {
        User user = getDefaultUser();
        Chat chat = createChat();

        chatRepository.addUser(chat.getId(), user.getId()).block();

        webClient.delete().uri("/api/chats/{id}/memberships", chat.getId())
                .headers(withUser(user))
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void chatLeave_alreadyLeftChat() {
        User user = getDefaultUser();
        Chat chat = createChat();

        chatRepository.addUser(chat.getId(), user.getId()).block();
        chatRepository.removeUser(chat.getId(), user.getId()).block();

        webClient.delete().uri("/api/chats/{id}/memberships", chat.getId())
                .headers(withUser(user))
                .exchange()
                .expectStatus().isNotModified();
    }

    @Test
    void chatLeave_neverJoinedChat() {
        Chat chat = createChat();
        webClient.delete().uri("/api/chats/{id}/memberships", chat.getId())
                .headers(withUser())
                .exchange()
                .expectStatus().isNotModified();
    }

    @Test
    void chatLeave_notFound() {
        webClient.delete().uri("/api/chats/{id}/memberships", "123")
                .headers(withUser())
                .exchange()
                .expectStatus().isNotFound();
    }
}
