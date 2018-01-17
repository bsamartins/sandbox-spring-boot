package com.github.bsamartins.springboot.notifications.repository;

import com.github.bsamartins.springboot.notifications.domain.persistence.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveMongoRepository<User, String> {
    Mono<User> findByUsername(String username);
}
