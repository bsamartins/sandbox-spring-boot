package com.github.bsamartins.springboot.notifications.repository;

import com.github.bsamartins.springboot.notifications.domain.persistence.Group;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface ChatMembershipRepository extends ReactiveMongoRepository<ChatMembership, String> {

    Flux<Group> findAllBy(TextCriteria criteria);

}
