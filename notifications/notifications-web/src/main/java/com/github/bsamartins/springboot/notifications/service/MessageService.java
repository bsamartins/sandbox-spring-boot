package com.github.bsamartins.springboot.notifications.service;

import com.github.bsamartins.springboot.notifications.domain.persistence.Message;
import com.github.bsamartins.springboot.notifications.messaging.RedisMessagePublisher;
import com.github.bsamartins.springboot.notifications.repository.MessageRepository;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class MessageService {

    private static Logger log = LoggerFactory.getLogger(MessageService.class);

    @Autowired
    private RedisMessagePublisher messagePublisher;

    @Autowired
    private MessageRepository repository;

    @Autowired
    private Publisher<com.github.bsamartins.springboot.notifications.messaging.Message<Message>> messagePubSub;


    public Mono<Message> save(Message msg) {
        return repository.save(msg)
                .doOnNext(m -> messagePublisher.publish(m));
    }

    public Mono<List<Message>> getMessages() {
        return repository.findAll()
                .collectList();
    }

    public Flux<Message> stream() {
        return Flux.from(this.messagePubSub)
                .map(com.github.bsamartins.springboot.notifications.messaging.Message::getPayload);
    }

}