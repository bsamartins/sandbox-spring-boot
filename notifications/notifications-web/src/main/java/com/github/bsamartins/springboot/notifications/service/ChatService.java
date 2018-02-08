package com.github.bsamartins.springboot.notifications.service;

import com.github.bsamartins.springboot.notifications.domain.File;
import com.github.bsamartins.springboot.notifications.domain.persistence.Chat;
import com.github.bsamartins.springboot.notifications.domain.persistence.User;
import com.github.bsamartins.springboot.notifications.repository.ChatRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;

import static com.github.bsamartins.springboot.notifications.InputStreamUtils.toAsyncInputStream;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private FileService fileService;

    public Mono<Chat> findById(String id) {
        return chatRepository.findById(id);
    }

    public Flux<Chat> findAll() {
        return chatRepository.findAll();
    }

    public Mono<Chat> create(Chat chat, File chatPicture) {
        return chatRepository.save(chat)
                .log()
                .flatMap(g -> saveChatPicture(g, chatPicture))
                .log();
    }

    @NotNull
    private Mono<Chat> saveChatPicture(Chat chat, File chatPicture) {
        return savePicture(chat, chatPicture).flatMap(pid -> {
            chat.setPictureId(pid);
            return chatRepository.save(chat);
        });
    }

    private Mono<String> savePicture(Chat chat, File file) {
        return fileService.store(toAsyncInputStream(
                new ByteArrayInputStream(file.getContent())),
                String.format("/chats/%s", chat.getId()),
                file.getMediaType())
                .log();
    }

    public Mono<Void> join(Chat chat, User user) {
        return this.chatRepository.isUserInChat(chat.getId(), user.getId())
                .flatMap(e -> {
                    if(e) {
                        return Mono.error(new IllegalStateException("User already in group"));
                    } else {
                        return chatRepository.addUser(chat.getId(), user.getId());
                    }
                });
    }

    public Mono<Void> leave(Chat chat, User user) {
        return this.chatRepository.isUserInChat(chat.getId(), user.getId())
                .flatMap(e -> {
                    if(e) {
                        return chatRepository.removeUser(chat.getId(), user.getId());
                    } else {
                        return Mono.error(userNotInGroupError());
                    }
                });
    }

    private static Exception userNotInGroupError() {
        return new IllegalStateException("User not in group");
    }

    public Flux<Chat> findAll(String query) {
        return this.chatRepository.findByNameContaining(query);
    }
}
