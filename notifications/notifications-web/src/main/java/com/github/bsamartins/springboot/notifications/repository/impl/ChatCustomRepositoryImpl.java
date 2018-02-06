package com.github.bsamartins.springboot.notifications.repository.impl;

import com.github.bsamartins.springboot.notifications.domain.persistence.Chat;
import com.github.bsamartins.springboot.notifications.domain.persistence.ChatEvent;
import com.github.bsamartins.springboot.notifications.repository.ChatCustomRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Map;

import static com.github.bsamartins.springboot.notifications.domain.persistence.ChatEvent.Type.USER_JOINED;
import static com.github.bsamartins.springboot.notifications.domain.persistence.ChatEvent.Type.USER_LEFT;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Repository
public class ChatCustomRepositoryImpl implements ChatCustomRepository {

    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @Override
    public Mono<ChatEvent> findLastMembershipEventForChat(String chatId, String userId) {
        Aggregation aggregation = newAggregation(
                match(where("_id").is(new ObjectId(chatId))),
                unwind("events"),
                replaceRoot("events"),
                match(where("userId").is(userId)
                        .and("type").in(USER_LEFT.toString(), USER_JOINED.toString())),
                sort(Sort.Direction.DESC, "timestamp"));

        return reactiveMongoTemplate.aggregate(aggregation, "chats", ChatEvent.class)
                .take(1)
                .singleOrEmpty();
    }

    @Override
    public Mono<Void> addEvent(String chatId, ChatEvent chatEvent) {
        Query query = new Query(where("_id").is(chatId));
        return reactiveMongoTemplate.findAndModify(query, new Update().push("events", chatEvent), Chat.class)
                .then();
    }

    @Override
    public Mono<Integer> countEvents(String chatId) {
        Aggregation aggregation = newAggregation(
                match(where("_id").is(new ObjectId(chatId))),
                project().and("events").size().as("count"));

        return reactiveMongoTemplate.aggregate(aggregation, "chats", Map.class)
                .log()
                .singleOrEmpty()
                .map(r -> (Integer) r.getOrDefault("count", 0L));

    }

}
