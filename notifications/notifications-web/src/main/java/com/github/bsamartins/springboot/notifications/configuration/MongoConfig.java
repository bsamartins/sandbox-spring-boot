package com.github.bsamartins.springboot.notifications.configuration;

import com.github.bsamartins.springboot.notifications.configuration.support.DockerContainerBean;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.mapping.event.LoggingEventListener;
import org.springframework.data.mongodb.core.mapping.event.ValidatingMongoEventListener;
import org.springframework.util.SocketUtils;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import pt.bsamartins.spring.data.mongo.gridfs.ReactiveGridFsTemplate;

@Configuration
public class MongoConfig extends AbstractReactiveMongoConfiguration {

    @Bean
    public LoggingEventListener mongoEventListener() {
        return new LoggingEventListener();
    }

    @Override
    @Bean
    @DependsOn("mongoDockerContainer")
    public MongoClient reactiveMongoClient() {
        return MongoClients.create(String.format("mongodb://localhost:%d", mongoPort()));
    }

    @Override
    protected String getDatabaseName() {
        return "reactive";
    }

    @Bean
    public DockerContainerBean mongoDockerContainer(int mongoPort) {
        return new DockerContainerBean("mongo:latest", mongoPort, 27017);
    }

    @Bean
    public int mongoPort() {
        return SocketUtils.findAvailableTcpPort();
    }

    @Bean
    public ValidatingMongoEventListener validatingMongoEventListener() {
        return new ValidatingMongoEventListener(validator());
    }

    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate() throws Exception {
        return new ReactiveMongoTemplate(reactiveMongoDbFactory(), mappingMongoConverter());
    }

    @Bean public ReactiveGridFsTemplate reactiveGridFsTemplate() throws Exception {
        return new ReactiveGridFsTemplate(reactiveMongoDbFactory(), mappingMongoConverter());
    }
}