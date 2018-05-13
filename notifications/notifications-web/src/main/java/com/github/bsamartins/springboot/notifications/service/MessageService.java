package com.github.bsamartins.springboot.notifications.service;

import com.github.bsamartins.springboot.notifications.domain.persistence.Message;
import com.github.bsamartins.springboot.notifications.messaging.ChatStreams;
import com.github.bsamartins.springboot.notifications.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class MessageService {

    private static Logger log = LoggerFactory.getLogger(MessageService.class);

    @Autowired
    private MessageRepository repository;

    @Autowired
    private ChatStreams chatStreams;

    public Mono<Message> save(Message msg) {
        return repository.save(msg)
                .doOnNext(this::publishChatEvent);
    }

    public Mono<List<Message>> getMessages() {
        return repository.findAll()
                .collectList();
    }

    public Flux<Message> stream() {
        return Flux.empty();
//        return Flux.from(this.chatStreams.inboundGreetings())
//                .map(com.github.bsamartins.springboot.notifications.messaging.Message::getPayload);
    }

    private void publishChatEvent(Message msg) {
        chatStreams.outboundGreetings()
                .send(MessageBuilder
                        .withPayload(msg)
                        .build());
    }

}