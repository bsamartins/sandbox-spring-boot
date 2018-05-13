package com.github.bsamartins.springboot.notifications.test

import com.github.bsamartins.springboot.notifications.test.config.MongoTestConfig
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@EnableReactiveMongoRepositories(basePackages = ["com.github.bsamartins.springboot.notifications"])
@WebFluxTest(excludeAutoConfiguration = [
    MongoAutoConfiguration::class,
    MongoDataAutoConfiguration::class,
    MongoReactiveDataAutoConfiguration::class ])
@ContextConfiguration(classes = [MongoTestConfig::class])
abstract class MongoIntegrationTest {

    @Autowired
    private lateinit var reactiveMongoTemplate: ReactiveMongoTemplate

    @BeforeEach
    fun init() {}


    @AfterEach
    fun destroy() {
        reactiveMongoTemplate.getCollectionNames()
                .flatMap(reactiveMongoTemplate::dropCollection)
                .blockLast()

    }
}