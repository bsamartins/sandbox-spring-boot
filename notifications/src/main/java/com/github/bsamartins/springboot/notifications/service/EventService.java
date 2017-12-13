package com.github.bsamartins.springboot.notifications.service;

import com.github.bsamartins.springboot.notifications.domain.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import static com.github.bsamartins.springboot.notifications.configuration.ActiveMQConfig.ORDER_QUEUE;

@Service
public class EventService {

    private static Logger log = LoggerFactory.getLogger(EventService.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    public void send(Event myEvent) {
        log.info("sending with convertAndSend() to queue <" + myEvent + ">");
        jmsTemplate.convertAndSend(ORDER_QUEUE, myEvent);
    }
}