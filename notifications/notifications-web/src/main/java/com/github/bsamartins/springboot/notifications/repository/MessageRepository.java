package com.github.bsamartins.springboot.notifications.repository;

import com.github.bsamartins.springboot.notifications.domain.persistence.Message;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface MessageRepository extends ReactiveMongoRepository<Message, String> {
}
