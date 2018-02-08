package com.github.bsamartins.springboot.notifications.test;

import com.github.bsamartins.springboot.notifications.configuration.MongoConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@EnableReactiveMongoRepositories(basePackages = "com.github.bsamartins.springboot.notifications")
@WebFluxTest(excludeAutoConfiguration = { MongoAutoConfiguration.class, MongoDataAutoConfiguration.class, MongoReactiveDataAutoConfiguration.class })
@ContextConfiguration(classes = MongoIntegrationTest.MongoTestConfig.class)
@TestPropertySource("classpath:application.test.properties")
public abstract class MongoIntegrationTest {

    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @BeforeEach
    final public void init() {}


    @AfterEach
    final public void destroy() {
        reactiveMongoTemplate.getCollectionNames()
                .flatMap(reactiveMongoTemplate::dropCollection)
                .blockLast();

    }

    @Configuration
    public static class MongoTestConfig extends MongoConfig {
    }

}
