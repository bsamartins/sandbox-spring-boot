package com.github.bsamartins.springboot.notifications.test.config

import com.github.bsamartins.springboot.notifications.configuration.MongoConfig
import com.github.bsamartins.springboot.notifications.utils.KGenericContainer
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.testcontainers.containers.GenericContainer

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
        return MongoClients.create("mongodb://localhost:${getMongoPort()}")
    }
}