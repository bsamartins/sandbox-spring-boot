package com.github.bsamartins.springboot.notifications.test;

import com.github.bsamartins.springboot.notifications.configuration.MongoConfig;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.Wait;
import org.testcontainers.containers.wait.WaitStrategy;

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
        @Bean
        public GenericContainer mongoDockerContainer() {
            GenericContainer container = new GenericContainer("mongo:latest")
                    .withExposedPorts(27017);
            container.start();
            return container;
        }

        private int getMongoPort() {
            return mongoDockerContainer().getMappedPort(27017);
        }

        @Override
        public MongoClient reactiveMongoClient() {
            return MongoClients.create(String.format("mongodb://localhost:%d", getMongoPort()));
        }
    }

}
