package com.github.bsamartins.springboot.notifications.service;

import com.github.bsamartins.springboot.notifications.domain.persistence.Message;
import com.github.bsamartins.springboot.notifications.persistence.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.github.bsamartins.springboot.notifications.configuration.ActiveMQConfig.ORDER_QUEUE;

@Service
public class MessageService {

    private static Logger log = LoggerFactory.getLogger(MessageService.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private MessageRepository repository;

    public Mono<Message> save(Message msg) {
        return Mono.justOrEmpty(msg)
                .map(repository::save)
                .map(entity -> {
                    log.info("sending with convertAndSend() to queue <" + entity + ">");
                    jmsTemplate.convertAndSend(ORDER_QUEUE, entity);
                    return entity;
                });
    }

    public Mono<Iterable<Message>> getMessages() {
        return Mono.just(repository.findAll());
    }
}