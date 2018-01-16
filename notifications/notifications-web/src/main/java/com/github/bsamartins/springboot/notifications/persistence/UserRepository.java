package com.github.bsamartins.springboot.notifications.persistence;

import com.github.bsamartins.springboot.notifications.domain.persistence.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveMongoRepository<User, String> {
    Mono<User> findByUsername(String username);
}
