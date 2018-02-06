package com.github.bsamartins.springboot.notifications.repository;

import com.github.bsamartins.springboot.notifications.domain.persistence.ChatEvent;
import reactor.core.publisher.Mono;

public interface ChatCustomRepository {

    Mono<ChatEvent> findLastMembershipEventForChat(String chatId, String userId);

    Mono<Void> addEvent(String chatId, ChatEvent chatEvent);

    Mono<Integer> countEvents(String chatId);

}
