package com.github.bsamartins.springboot.notifications.controller;

import com.github.bsamartins.springboot.notifications.domain.persistence.Group;
import com.github.bsamartins.springboot.notifications.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @GetMapping(value = "/{id}")
    public Mono<ResponseEntity<Group>> findById(@PathVariable("id") String id) {
        return groupService.findById(id)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @GetMapping
    public Mono<List<Group>> findAll() {
        return groupService.findAll().collectList();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Group> create(@RequestBody Group.GroupCreate group) {
        return groupService.create(new Group(group), group.getPicture())
                .cast(Group.class)
                .switchIfEmpty(Mono.error(new Exception("what?")))
                .log();
    }

}
