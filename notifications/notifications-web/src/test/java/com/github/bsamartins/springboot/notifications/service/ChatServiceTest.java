package com.github.bsamartins.springboot.notifications.service;

import com.github.bsamartins.springboot.notifications.domain.persistence.Chat;
import com.github.bsamartins.springboot.notifications.domain.persistence.User;
import com.github.bsamartins.springboot.notifications.repository.ChatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @InjectMocks
    private ChatService chatService;

    @Mock
    private ChatRepository chatRepository;

    @BeforeEach
    void setup() {
        chatService = new ChatService();
    }

    @Test
    void join() {
        Chat chat = new Chat();
        User user = new User();

        when(chatRepository.isUserInChat(chat.getId(), user.getId())).thenReturn(Mono.just(false));
        when(chatRepository.addUser(chat.getId(), user.getId())).thenReturn(Mono.empty());

        StepVerifier.create(chatService.join(chat, user))
                .verifyComplete();

        verify(chatRepository);
    }

    @Test
    void join_alreadyInChat() {
        Chat chat = new Chat();
        User user = new User();

        when(chatRepository.isUserInChat(chat.getId(), user.getId())).thenReturn(Mono.just(true));

        StepVerifier.create(chatService.join(chat, user))
                .verifyError(IllegalStateException.class);

        verify(chatRepository);
    }

    @Test
    void leave_notInChat() {
        Chat chat = new Chat();
        User user = new User();

        when(chatRepository.isUserInChat(chat.getId(), user.getId())).thenReturn(Mono.just(false));

        StepVerifier.create(chatService.leave(chat, user))
                .verifyError(IllegalStateException.class);

        verify(chatRepository);
    }

    @Test
    void leave() {
        Chat chat = new Chat();
        User user = new User();

        when(chatRepository.isUserInChat(chat.getId(), user.getId())).thenReturn(Mono.just(true));
        when(chatRepository.removeUser(chat.getId(), user.getId())).thenReturn(Mono.empty());

        StepVerifier.create(chatService.leave(chat, user))
                .verifyComplete();

        verify(chatRepository);
    }
}
