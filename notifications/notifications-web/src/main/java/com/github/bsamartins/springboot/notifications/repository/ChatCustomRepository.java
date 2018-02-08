package com.github.bsamartins.springboot.notifications.repository;

import com.github.bsamartins.springboot.notifications.domain.persistence.Chat;
import com.github.bsamartins.springboot.notifications.domain.persistence.ChatEvent;
import org.springframework.data.mongodb.repository.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ChatCustomRepository {

    Mono<Void> addUser(String chatId, String userId);

    Mono<Void> removeUser(String chatId, String userId);

    Mono<Boolean> isUserInChat(String chatId, String userId);

    Mono<ChatEvent> findLastEventForUser(String chatId, String userId, ChatEvent.Type...type);

    Flux<String> findUsers(String chatId);
}
