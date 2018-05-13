package com.github.bsamartins.springboot.notifications.test

import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.KafkaContainer

class KafkaApplicationContextInitializer: ApplicationContextInitializer<ConfigurableApplicationContext> {
    override fun initialize(applicationContext: ConfigurableApplicationContext?) {
        val hasBeans = applicationContext?.beanDefinitionNames?.isNotEmpty() ?: false
        if(hasBeans) {
            applicationContext?.beanDefinitionNames?.forEach { it -> println("==> $it") }
        } else {
            println("===> No beans")
        }

        val container = KafkaContainer()
                .withExposedPorts(KafkaContainer.KAFKA_PORT)
        container.start()
        val port = container.getMappedPort(KafkaContainer.KAFKA_PORT)
        System.setProperty("spring.cloud.stream.kafka.binder.brokers", "localhost:$port")
    }
}