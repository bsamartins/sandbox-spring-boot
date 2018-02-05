package com.github.bsamartins.springboot.notifications.service;

import com.github.bsamartins.springboot.notifications.domain.File;
import com.github.bsamartins.springboot.notifications.domain.persistence.Group;
import com.github.bsamartins.springboot.notifications.repository.GroupRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;

import static com.github.bsamartins.springboot.notifications.InputStreamUtils.toAsyncInputStream;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private FileService fileService;

    public Mono<Group> findById(String id) {
        return groupRepository.findById(id);
    }

    public Flux<Group> findAll() {
        return groupRepository.findAll();
    }

    public Mono<Group> create(Group group, File groupPicture) {
        return groupRepository.save(group)
                .log()
                .flatMap(g -> saveGroupPicture(g, groupPicture))
                .log();
    }

    @NotNull
    private Mono<Group> saveGroupPicture(Group group, File groupPicture) {
        return savePicture(group, groupPicture).flatMap(pid -> {
            group.setPictureId(pid);
            return groupRepository.save(group);
        });
    }

    private Mono<String> savePicture(Group group, File file) {
        return fileService.store(toAsyncInputStream(
                new ByteArrayInputStream(file.getContent())),
                String.format("/groups/%s", group.getId()),
                file.getMediaType())
                .log();
    }
}
