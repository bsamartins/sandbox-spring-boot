package com.github.bsamartins.springboot.notifications;

import com.github.bsamartins.springboot.notifications.domain.Event;
import com.github.bsamartins.springboot.notifications.service.EventService;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.messaging.Message;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Date;

@RestController
public class MessageController {

    @Autowired
    private EventService eventService;

    @Autowired
    private Publisher<Message<Event>> jmsReactiveSource;

    @RequestMapping(value = "/api/messages/new", method = RequestMethod.GET)
    public void sendMessage(@RequestParam("message") String input) {
        Event event = new Event();
        event.setContent(input);
        event.setTimestamp(new Date());
        eventService.send(event);
    }

    @RequestMapping(value = "/api/messages", method = RequestMethod.GET, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Event> getMessages() {
        return Flux.from(jmsReactiveSource)
                .map(Message::getPayload);
    }

}
