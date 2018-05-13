package com.github.bsamartins.springboot.notifications.service

import com.github.bsamartins.springboot.notifications.domain.persistence.Chat
import com.github.bsamartins.springboot.notifications.domain.persistence.User
import com.github.bsamartins.springboot.notifications.repository.ChatRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoSettings

@ExtendWith(MockitoExtension::class)
@MockitoSettings
class ChatServiceTest {

    @InjectMocks
    private val chatService: ChatService = ChatService()

    @Mock
    private lateinit var chatRepository: ChatRepository

    @BeforeEach
    fun setup() {
    }

    @Test
    fun join() {
        val chat = Chat()
        val user = User()

        `when`(chatRepository.isUserInChat(chat.id, user.id)).thenReturn(Mono.just(false))
        `when`(chatRepository.addUser(chat.id, user.id)).thenReturn(Mono.empty())

        StepVerifier.create(chatService.join(chat, user))
                .verifyComplete()

        verify(chatRepository).isUserInChat(chat.id, user.id)
        verify(chatRepository).addUser(chat.id, user.id)
    }

    @Test
    fun join_alreadyInChat() {
        val chat = Chat()
        val user = User()

        `when`(chatRepository.isUserInChat(chat.id, user.id)).thenReturn(Mono.just(true))

        StepVerifier.create(chatService.join(chat, user))
                .verifyError(IllegalStateException::class.java)

        verify(chatRepository).isUserInChat(chat.id, user.id)
    }

    @Test
    fun leave_notInChat() {
        val chat = Chat()
        val user = User()

        `when`(chatRepository.isUserInChat(chat.id, user.id)).thenReturn(Mono.just(false))

        StepVerifier.create(chatService.leave(chat, user))
                .verifyError(IllegalStateException::class.java)

        verify(chatRepository).isUserInChat(chat.id, user.id)
    }

    @Test
    fun leave() {
        val chat = Chat()
        val user = User()

        `when`(chatRepository.isUserInChat(chat.id, user.id)).thenReturn(Mono.just(true))
        `when`(chatRepository.removeUser(chat.id, user.id)).thenReturn(Mono.empty())

        StepVerifier.create(chatService.leave(chat, user))
                .verifyComplete()

        verify(chatRepository).isUserInChat(chat.id, user.id)
        verify(chatRepository).removeUser(chat.id, user.id)
    }
}
