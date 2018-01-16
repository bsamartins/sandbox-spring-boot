package com.github.bsamartins.springboot.notifications.persistence;

import com.github.bsamartins.springboot.notifications.domain.persistence.Message;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

public interface MessageRepository extends ReactiveMongoRepository<Message, String> {
}
