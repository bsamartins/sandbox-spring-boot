package com.github.bsamartins.springboot.notifications.service;

import com.github.bsamartins.springboot.notifications.domain.persistence.Chat;
import com.github.bsamartins.springboot.notifications.domain.persistence.ChatEvent;
import com.github.bsamartins.springboot.notifications.domain.persistence.User;
import com.github.bsamartins.springboot.notifications.repository.ChatRepository;
import org.easymock.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;

import static org.easymock.EasyMock.*;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ChatServiceTest {

    @TestSubject
    private ChatService chatService;

    @Mock(type = MockType.STRICT)
    private ChatRepository chatRepository;

    @BeforeEach
    void setup() {
        chatService = new ChatService();

        EasyMockSupport.injectMocks(this);
    }

    @Test
    void join() {
        Chat chat = new Chat();
        User user = new User();

        Capture<ChatEvent> chatEventCaptor = newCapture();

        expect(chatRepository.findLastMembershipEventForChat(chat.getId(), user.getId())).andReturn(Mono.empty());
        expect(chatRepository.addEvent(eq(chat.getId()), capture(chatEventCaptor))).andReturn(Mono.empty());

        replay(chatRepository);

        StepVerifier.create(chatService.join(chat, user))
                .verifyComplete();

        verify(chatRepository);

        ChatEvent event = chatEventCaptor.getValue();
        assertNotNull(event);

        assertEquals(user.getId(), event.getId());
        assertNotNull(event.getTimestamp());
        assertEquals(ChatEvent.Type.USER_JOINED, event.getType());
    }

    @Test
    void join_lastEventJoined() {
        Chat chat = new Chat();
        User user = new User();

        ChatEvent lastEvent = new ChatEvent();
        lastEvent.setType(ChatEvent.Type.USER_JOINED);
        lastEvent.setUserId(user.getId());
        lastEvent.setTimestamp(OffsetDateTime.now());

        Mono<Void> addEventResponse = Mono.empty();

        expect(chatRepository.findLastMembershipEventForChat(chat.getId(), user.getId())).andReturn(Mono.just(lastEvent));
        expect(chatRepository.addEvent(eq(chat.getId()), anyObject(ChatEvent.class))).andReturn(addEventResponse);

        replay(chatRepository);

        StepVerifier.create(chatService.join(chat, user))
                .verifyError(IllegalStateException.class);

        StepVerifier.create(addEventResponse)
                .expectNextCount(0)
                .verifyComplete();

        verify(chatRepository);
    }

    @Test
    void join_lastEventLeft() {
        Chat chat = new Chat();
        User user = new User();

        ChatEvent lastEvent = new ChatEvent();
        lastEvent.setType(ChatEvent.Type.USER_LEFT);
        lastEvent.setUserId(user.getId());
        lastEvent.setTimestamp(OffsetDateTime.now());

        Mono<Void> addEventResponse = Mono.empty();

        expect(chatRepository.findLastMembershipEventForChat(chat.getId(), user.getId())).andReturn(Mono.just(lastEvent));
        expect(chatRepository.addEvent(eq(chat.getId()), anyObject(ChatEvent.class))).andReturn(addEventResponse);

        replay(chatRepository);

        StepVerifier.create(chatService.join(chat, user))
                .verifyComplete();

        StepVerifier.create(addEventResponse)
                .expectSubscription()
                .verifyComplete();

        verify(chatRepository);
    }

    @Test
    void leave_notJoined() {
        Chat chat = new Chat();
        User user = new User();

        expect(chatRepository.findLastMembershipEventForChat(chat.getId(), user.getId())).andReturn(Mono.empty());

        replay(chatRepository);

        StepVerifier.create(chatService.leave(chat, user))
                .verifyError(IllegalStateException.class);

        verify(chatRepository);
    }

    @Test
    void leave_lastEventLeft() {
        Chat chat = new Chat();
        User user = new User();

        ChatEvent lastEvent = new ChatEvent();
        lastEvent.setType(ChatEvent.Type.USER_LEFT);
        lastEvent.setUserId(user.getId());
        lastEvent.setTimestamp(OffsetDateTime.now());

        Mono<Void> addEventResponse = Mono.empty();

        expect(chatRepository.findLastMembershipEventForChat(chat.getId(), user.getId())).andReturn(Mono.just(lastEvent));

        replay(chatRepository);

        StepVerifier.create(chatService.leave(chat, user))
                .verifyError(IllegalStateException.class);

        StepVerifier.create(addEventResponse)
                .expectNextCount(0)
                .verifyComplete();

        verify(chatRepository);
    }

    @Test
    void leave_lastEventJoined() {
        Chat chat = new Chat();
        User user = new User();

        ChatEvent lastEvent = new ChatEvent();
        lastEvent.setType(ChatEvent.Type.USER_JOINED);
        lastEvent.setUserId(user.getId());
        lastEvent.setTimestamp(OffsetDateTime.now());

        Mono<Void> addEventResponse = Mono.empty();

        expect(chatRepository.findLastMembershipEventForChat(chat.getId(), user.getId())).andReturn(Mono.just(lastEvent));
        expect(chatRepository.addEvent(eq(chat.getId()), anyObject(ChatEvent.class))).andReturn(addEventResponse);

        replay(chatRepository);

        StepVerifier.create(chatService.leave(chat, user))
                .verifyComplete();

        StepVerifier.create(addEventResponse)
                .expectSubscription()
                .verifyComplete();

        verify(chatRepository);
    }
}
