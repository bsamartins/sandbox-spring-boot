package com.github.bsamartins.springboot.notifications.controller;

import com.github.bsamartins.springboot.notifications.domain.persistence.Message;
import com.github.bsamartins.springboot.notifications.security.CustomUser;
import com.github.bsamartins.springboot.notifications.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

import static com.github.bsamartins.springboot.notifications.controller.helper.SseHelper.sse;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @RequestMapping(method = RequestMethod.POST)
    public Mono<Message> sendMessage(@RequestBody String input, @AuthenticationPrincipal CustomUser authUser) {
        return Mono.just(input).map(text -> {
            Message message = new Message();
            message.setText(text);
            message.setTimestamp(LocalDateTime.now());
            message.setUserId(authUser.getUser().getId());
            return message;
        }).flatMap(messageService::save);
    }

    @RequestMapping(value = "/stream", method = RequestMethod.GET, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<Message>> streamMessages() {
        return messageService.stream()
                .map(msg -> ServerSentEvent.builder(msg).build())
                .mergeWith(sse());
    }

    @RequestMapping(method = RequestMethod.GET)
    public Mono<List<Message>> getMessages() {
        return messageService.getMessages();
    }

}
