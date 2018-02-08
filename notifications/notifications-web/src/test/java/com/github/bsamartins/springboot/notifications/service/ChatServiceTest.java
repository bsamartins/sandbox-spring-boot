package com.github.bsamartins.springboot.notifications.service;

import com.github.bsamartins.springboot.notifications.domain.persistence.Chat;
import com.github.bsamartins.springboot.notifications.domain.persistence.User;
import com.github.bsamartins.springboot.notifications.repository.ChatRepository;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.easymock.MockType;
import org.easymock.TestSubject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.easymock.EasyMock.*;

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

        expect(chatRepository.isUserInChat(chat.getId(), user.getId())).andReturn(Mono.just(false));
        expect(chatRepository.addUser(chat.getId(), user.getId())).andReturn(Mono.empty());

        replay(chatRepository);

        StepVerifier.create(chatService.join(chat, user))
                .verifyComplete();

        verify(chatRepository);
    }

    @Test
    void join_alreadyInChat() {
        Chat chat = new Chat();
        User user = new User();

        expect(chatRepository.isUserInChat(chat.getId(), user.getId())).andReturn(Mono.just(true));

        replay(chatRepository);

        StepVerifier.create(chatService.join(chat, user))
                .verifyError(IllegalStateException.class);

        verify(chatRepository);
    }

    @Test
    void leave_notInChat() {
        Chat chat = new Chat();
        User user = new User();

        expect(chatRepository.isUserInChat(chat.getId(), user.getId())).andReturn(Mono.just(false));

        replay(chatRepository);

        StepVerifier.create(chatService.leave(chat, user))
                .verifyError(IllegalStateException.class);

        verify(chatRepository);
    }

    @Test
    void leave() {
        Chat chat = new Chat();
        User user = new User();

        expect(chatRepository.isUserInChat(chat.getId(), user.getId())).andReturn(Mono.just(true));
        expect(chatRepository.removeUser(chat.getId(), user.getId())).andReturn(Mono.empty());

        replay(chatRepository);

        StepVerifier.create(chatService.leave(chat, user))
                .verifyComplete();

        verify(chatRepository);
    }
}
