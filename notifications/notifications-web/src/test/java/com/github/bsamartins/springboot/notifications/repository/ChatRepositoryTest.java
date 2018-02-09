package com.github.bsamartins.springboot.notifications.repository;

import com.github.bsamartins.springboot.notifications.domain.persistence.Chat;
import com.github.bsamartins.springboot.notifications.domain.persistence.User;
import com.github.bsamartins.springboot.notifications.test.MongoIntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

import java.util.UUID;

import static com.github.bsamartins.springboot.notifications.domain.persistence.ChatEvent.Type.USER_JOINED;
import static com.github.bsamartins.springboot.notifications.domain.persistence.ChatEvent.Type.USER_LEFT;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@EnableReactiveMongoRepositories
class ChatRepositoryTest extends MongoIntegrationTest {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findLastChatEventForUser() {

        Chat chat = new Chat();
        chat.setPictureId(UUID.randomUUID().toString());
        chat.setName("test");

        chatRepository.save(chat).block();

        User u1 = new User();
        u1.setUsername("u1");
        u1.setPassword("");

        User u2 = new User();
        u2.setUsername("u2");
        u2.setPassword("");

        userRepository.save(u1).block();
        userRepository.save(u2).block();

        chatRepository.addUser(chat.getId(), u1.getId()).block();
        chatRepository.removeUser(chat.getId(), u1.getId()).block();
        chatRepository.addUser(chat.getId(), u2.getId()).block();

        StepVerifier.create(chatRepository.findLastEventForUser(chat.getId(), u1.getId(), USER_LEFT))
                .assertNext(c -> assertEquals(c.getType(), USER_LEFT))
                .verifyComplete();
    }

    @Test
    void findLastChatEventForUser_noResults() {

        Chat chat = new Chat();
        chat.setPictureId(UUID.randomUUID().toString());
        chat.setName("test");

        chatRepository.save(chat).block();

        StepVerifier.create(chatRepository.findLastEventForUser(chat.getId(), "u1", USER_LEFT))
                .expectNextCount(0)
                .expectComplete()
                .verify();
    }

    @Test
    void findUsers() {
        Chat chat1 = createAndSaveChat("c1");
        Chat chat2 = createAndSaveChat("c2");

        User user1 = createAndSaveUser("u1");
        User user2 = createAndSaveUser("u2");
        User user3 = createAndSaveUser("u3");

        chatRepository.addUser(chat1.getId(), user1.getId()).block();
        chatRepository.addUser(chat1.getId(), user2.getId()).block();
        chatRepository.addUser(chat2.getId(), user3.getId()).block();

        StepVerifier.create(chatRepository.findUsers(chat1.getId()))
                .expectNext(user1.getId(), user2.getId())
                .verifyComplete();
    }

    @Test
    void findByUser() {
        Chat chat1 = createAndSaveChat("c1");
        Chat chat2 = createAndSaveChat("c2");
        Chat chat3 = createAndSaveChat("c3");

        User user1 = createAndSaveUser("u1");
        User user2 = createAndSaveUser("u2");

        chatRepository.addUser(chat1.getId(), user1.getId()).block();
        chatRepository.addUser(chat2.getId(), user1.getId()).block();
        chatRepository.addUser(chat2.getId(), user2.getId()).block();
        chatRepository.addUser(chat3.getId(), user2.getId()).block();

        StepVerifier.create(chatRepository.findAllByUser(user1.getId()))
                .assertNext(c -> assertEquals(chat1.getId(), c.getId()))
                .assertNext(c -> assertEquals(chat2.getId(), c.getId()))
                .verifyComplete();
    }

    @Test
    void findUsers_noUsers() {
        Chat chat1 = createAndSaveChat("c1");
        StepVerifier.create(chatRepository.findUsers(chat1.getId()))
                .expectComplete();
    }

    @Test
    void addUser() {
        Chat chat1 = createAndSaveChat("c1");
        User user1 = createAndSaveUser("u1");

        StepVerifier.create(chatRepository.addUser(chat1.getId(), user1.getId()))
            .verifyComplete();

        StepVerifier.create(chatRepository.findUsers(chat1.getId()))
                .expectNext(user1.getId())
                .verifyComplete();

        StepVerifier.create(chatRepository.findLastEventForUser(chat1.getId(), user1.getId(), null))
                .assertNext(e -> {
                    assertEquals(user1.getId(), e.getUserId());
                    assertEquals(USER_JOINED, e.getType());
                }).verifyComplete();
    }

    @Test
    void removeUser() {
        Chat chat1 = createAndSaveChat("c1");
        User user1 = createAndSaveUser("u1");

        StepVerifier.create(chatRepository.removeUser(chat1.getId(), user1.getId()))
                .verifyComplete();

        StepVerifier.create(chatRepository.findUsers(chat1.getId()))
                .verifyComplete();

        StepVerifier.create(chatRepository.findLastEventForUser(chat1.getId(), user1.getId(), null))
            .assertNext(e -> {
                assertEquals(user1.getId(), e.getUserId());
                assertEquals(USER_LEFT, e.getType());
            }).verifyComplete();
    }

    @Test
    void isUserInChat() {
        Chat chat1 = createAndSaveChat("c1");
        User user1 = createAndSaveUser("u1");

        StepVerifier.create(chatRepository.addUser(chat1.getId(), user1.getId()))
                .verifyComplete();

        StepVerifier.create(chatRepository.isUserInChat(chat1.getId(), user1.getId()))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void isUserInChat_not() {
        Chat chat1 = createAndSaveChat("c1");
        StepVerifier.create(chatRepository.isUserInChat(chat1.getId(), "x"))
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void findByNameContaining() {
        createAndSaveChat("super dupa chat");
        createAndSaveChat("regular chat");
        StepVerifier.create(chatRepository.findByNameContaining("chat"))
                .expectNextCount(2)
                .verifyComplete();

        StepVerifier.create(chatRepository.findByNameContaining("sup   cha"))
                .expectNextCount(1)
                .verifyComplete();

        StepVerifier.create(chatRepository.findByNameContaining("ChAt"))
                .expectNextCount(2)
                .verifyComplete();
    }

    private User createAndSaveUser(String username) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("");
        return userRepository.save(user).block();
    }
    private Chat createAndSaveChat(String name) {
        Chat chat = new Chat();
        chat.setName(name);
        chat.setPictureId(UUID.randomUUID().toString());
        return chatRepository.save(chat).block();
    }


}
