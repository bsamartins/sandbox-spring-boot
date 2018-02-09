package com.github.bsamartins.springboot.notifications.controller;

import com.github.bsamartins.springboot.notifications.domain.ChatCreate;
import com.github.bsamartins.springboot.notifications.domain.persistence.Chat;
import com.github.bsamartins.springboot.notifications.security.CustomUser;
import com.github.bsamartins.springboot.notifications.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @GetMapping(value = "/{id}")
    public Mono<ResponseEntity<Chat>> findById(@PathVariable("id") String id) {
        return chatService.findById(id)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @GetMapping
    public Mono<List<Chat>> findAll(@AuthenticationPrincipal CustomUser authUser) {
        return chatService.findAllForUser(authUser.getUser())
                .collectList();
    }

    @GetMapping(params = "query")
    public Mono<List<Chat>> findAllByQuery(@RequestParam("query") String query) {
        return chatService.findAll(query).collectList();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Chat> create(@RequestBody ChatCreate chat) {
        return chatService.create(new Chat(chat), chat.getPicture())
                .cast(Chat.class)
                .switchIfEmpty(Mono.error(new Exception("what?")));
    }

    @GetMapping(value = "/{id}/users")
    public Mono<ResponseEntity<List<String>>> joinChat(@PathVariable("id") String chatId) {
        return chatService.findById(chatId)
                .flatMap(chat -> chatService.findUsersByChat(chatId)
                        .collectList()
                        .map(result -> ResponseEntity.ok().body(result)))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @PostMapping(value = "/{id}/users")
    public Mono<ResponseEntity> joinChat(@PathVariable("id") String chatId,
                                     @AuthenticationPrincipal CustomUser authUser) {
        return chatService.findById(chatId)
                .flatMap(chat -> chatService.join(chat, authUser.getUser())
                        .cast(ResponseEntity.class)
                        .onErrorReturn(IllegalStateException.class, ResponseEntity.status(HttpStatus.NOT_MODIFIED).build())
                        .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.CREATED).build())))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @DeleteMapping(value = "/{id}/users")
    public Mono<ResponseEntity> leaveChat(@PathVariable("id") String chatId,
                                         @AuthenticationPrincipal CustomUser authUser) {
        return chatService.findById(chatId)
                .flatMap(chat -> chatService.leave(chat, authUser.getUser())
                        .cast(ResponseEntity.class)
                        .onErrorReturn(IllegalStateException.class, ResponseEntity.status(HttpStatus.NOT_MODIFIED).build())
                        .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.OK).build())))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }
}
