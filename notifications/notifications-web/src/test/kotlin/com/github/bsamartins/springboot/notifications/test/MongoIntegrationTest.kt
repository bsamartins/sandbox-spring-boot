package com.github.bsamartins.springboot.notifications.test

import com.github.bsamartins.springboot.notifications.configuration.MongoConfig
import com.github.bsamartins.springboot.notifications.utils.KGenericContainer
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.testcontainers.containers.GenericContainer

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

@Configuration
open class MongoTestConfig: MongoConfig() {
    @Bean
    open fun mongoDockerContainer(): GenericContainer<*> {
        val container = KGenericContainer("mongo:latest")
            .withExposedPorts(27017)
        container.start()
        return container
    }

    private fun getMongoPort(): Int {
        return mongoDockerContainer().getMappedPort(27017)
    }

    @Override
    override fun reactiveMongoClient(): MongoClient {
        return MongoClients.create(String.format("mongodb://localhost:%d", getMongoPort()));
    }
}

