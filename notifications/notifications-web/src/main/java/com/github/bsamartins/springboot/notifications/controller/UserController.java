package com.github.bsamartins.springboot.notifications.controller;

import com.github.bsamartins.springboot.notifications.domain.persistence.User;
import com.github.bsamartins.springboot.notifications.repository.UserRepository;
import com.github.bsamartins.springboot.notifications.security.CustomUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping(value = "/me")
    public Mono<User> me(@AuthenticationPrincipal CustomUser authUser) {
        return Mono.just(authUser.getUser());
    }

    @GetMapping(value = "/{id}")
    public Mono<ResponseEntity<User>> findById(@PathVariable("id") String id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

}
