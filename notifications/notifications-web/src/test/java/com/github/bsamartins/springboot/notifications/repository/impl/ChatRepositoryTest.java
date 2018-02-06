package com.github.bsamartins.springboot.notifications.repository.impl;

import com.github.bsamartins.springboot.notifications.domain.persistence.Chat;
import com.github.bsamartins.springboot.notifications.domain.persistence.ChatEvent;
import com.github.bsamartins.springboot.notifications.repository.ChatRepository;
import com.github.bsamartins.springboot.notifications.test.MongoIntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@EnableReactiveMongoRepositories
public class ChatRepositoryTest extends MongoIntegrationTest {

    @Autowired
    private ChatRepository chatRepository;

    @Test
    public void findLastMembershipEventForChat() {

        Chat chat = new Chat();
        chat.setPictureId(UUID.randomUUID().toString());
        chat.setName("test");

        chatRepository.save(chat).block();

        ChatEvent ce1 = new ChatEvent();
        ce1.setUserId("u1");
        ce1.setType(ChatEvent.Type.USER_JOINED);
        ce1.setTimestamp(OffsetDateTime.now());

        ChatEvent ce2 = new ChatEvent();
        ce2.setUserId("u2");
        ce2.setType(ChatEvent.Type.USER_JOINED);
        ce2.setTimestamp(OffsetDateTime.now());

        ChatEvent ce3 = new ChatEvent();
        ce3.setUserId("u1");
        ce3.setType(ChatEvent.Type.USER_LEFT);
        ce3.setTimestamp(OffsetDateTime.now());

        chatRepository.addEvent(chat.getId(), ce1).block();
        chatRepository.addEvent(chat.getId(), ce2).block();
        chatRepository.addEvent(chat.getId(), ce3).block();

        StepVerifier.create(chatRepository.findLastMembershipEventForChat(chat.getId(), "u1"))
                .assertNext(c -> assertEquals(ce3.getId(), c.getId()))
                .expectComplete()
                .verify();
    }

    @Test
    public void findLastMembershipEventForChat_noResults() {

        Chat chat = new Chat();
        chat.setPictureId(UUID.randomUUID().toString());
        chat.setName("test");

        chatRepository.save(chat).block();

        StepVerifier.create(chatRepository.findLastMembershipEventForChat(chat.getId(), "u1"))
                .expectNextCount(0)
                .expectComplete()
                .verify();
    }

    @Test
    public void addChatEvent() {

        Chat chat = new Chat();
        chat.setPictureId(UUID.randomUUID().toString());
        chat.setName("test");

        chatRepository.save(chat).block();

        ChatEvent ce1 = new ChatEvent();
        ce1.setUserId("u1");
        ce1.setType(ChatEvent.Type.USER_JOINED);
        ce1.setTimestamp(OffsetDateTime.now());

        StepVerifier.create(chatRepository.addEvent(chat.getId(), ce1))
                .expectComplete()
                .verify();

        StepVerifier.create(chatRepository.countEvents(chat.getId()))
                .expectNext(1)
                .verifyComplete();
    }
}
