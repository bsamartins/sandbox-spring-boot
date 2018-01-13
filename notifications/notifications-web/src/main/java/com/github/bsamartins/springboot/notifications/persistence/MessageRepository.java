package com.github.bsamartins.springboot.notifications.persistence;

import com.github.bsamartins.springboot.notifications.domain.persistence.Message;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends CrudRepository<Message, Long> {
}
