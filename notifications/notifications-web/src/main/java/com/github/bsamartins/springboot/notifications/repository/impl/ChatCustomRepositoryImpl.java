package com.github.bsamartins.springboot.notifications.repository.impl;

import com.github.bsamartins.springboot.notifications.domain.persistence.Chat;
import com.github.bsamartins.springboot.notifications.domain.persistence.ChatEvent;
import com.github.bsamartins.springboot.notifications.repository.ChatCustomRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.github.bsamartins.springboot.notifications.domain.persistence.ChatEvent.Type.USER_JOINED;
import static com.github.bsamartins.springboot.notifications.domain.persistence.ChatEvent.Type.USER_LEFT;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Repository
public class ChatCustomRepositoryImpl implements ChatCustomRepository {

    private static final String COLLECTION_NAME = "chats";

    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @Override
    public Mono<ChatEvent> findLastEventForUser(String chatId, String userId, ChatEvent.Type ...types) {
        Criteria eventCriteria = where("userId").is(userId);
        if(types != null) {
            List<String> eventTypes = Arrays.stream(types)
                    .map(x -> x.toString())
                    .collect(toList());
            eventCriteria.and("type").in(eventTypes);
        }
        Aggregation aggregation = newAggregation(
                match(byId(chatId)),
                unwind("events"),
                replaceRoot("events"),
                match(eventCriteria),
                sort(Sort.Direction.DESC, "timestamp"));

        return reactiveMongoTemplate.aggregate(aggregation, COLLECTION_NAME, ChatEvent.class)
                .take(1)
                .singleOrEmpty();
    }

    @Override
    public Mono<Void> addUser(String chatId, String userId) {
        ChatEvent chatEvent = new ChatEvent();
        chatEvent.setType(USER_JOINED);
        chatEvent.setTimestamp(OffsetDateTime.now());
        chatEvent.setUserId(userId);

        Query query = new Query(byId(chatId));
        Update update = new Update().addToSet("users", userId)
                .push("events", chatEvent);
        return reactiveMongoTemplate.findAndModify(query, update, Chat.class)
                .then();
    }

    @Override
    public Mono<Void> removeUser(String chatId, String userId) {
        ChatEvent chatEvent = new ChatEvent();
        chatEvent.setType(USER_LEFT);
        chatEvent.setTimestamp(OffsetDateTime.now());
        chatEvent.setUserId(userId);

        Query query = new Query(where("_id").is(chatId));
        Update update = new Update().pull("users", userId)
                .push("events", chatEvent);
        return reactiveMongoTemplate.findAndModify(query, update, Chat.class)
                .then();
    }

    @Override
    public Mono<Boolean> isUserInChat(String chatId, String userId) {
        Query query = new Query(byId(chatId).and("users").is(userId));
        return reactiveMongoTemplate.find(query, Chat.class)
                .singleOrEmpty()
                .map(r -> true)
                .switchIfEmpty(Mono.just(false));
    }

    private static Criteria byId(String id) {
        return where("_id").is(new ObjectId(id));
    }

    private static Criteria byId(ObjectId id) {
        return where("_id").is(id);
    }

    @Override
    public Flux<String> findUsers(String chatId) {
        Aggregation aggregation = newAggregation(
                match(byId(chatId)),
                unwind("users"),
                project("users").andExclude("_id"));

        return reactiveMongoTemplate.aggregate(aggregation, COLLECTION_NAME, Map.class)
                .map(x -> (String) x.get("users"));
    }
}
