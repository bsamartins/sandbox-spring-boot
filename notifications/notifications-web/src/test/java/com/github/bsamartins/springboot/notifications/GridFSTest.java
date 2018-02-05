package com.github.bsamartins.springboot.notifications;

import com.github.bsamartins.springboot.notifications.configuration.MongoConfig;
import com.github.bsamartins.springboot.notifications.data.mongo.ReadableByteChannelAsyncInputStream;
import com.mongodb.reactivestreams.client.gridfs.AsyncInputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.core.style.ToStringCreator;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pt.bsamartins.spring.data.mongo.gridfs.ReactiveGridFsResource;
import pt.bsamartins.spring.data.mongo.gridfs.ReactiveGridFsTemplate;
import reactor.core.publisher.Mono;

import java.io.FileInputStream;

import static com.github.bsamartins.springboot.notifications.data.mongo.MongoHelper.toByteBuffer;

@ExtendWith(SpringExtension.class)
@EnableReactiveMongoRepositories
@ContextConfiguration(classes = MongoConfig.class)
@WebFluxTest(excludeAutoConfiguration = { MongoAutoConfiguration.class, MongoDataAutoConfiguration.class, MongoReactiveDataAutoConfiguration.class })
public class GridFSTest {

    @Autowired
    private ReactiveGridFsTemplate reactiveGridFsTemplate;

    @Test
    public void gridFs() throws Exception {
        FileInputStream inputStream = new FileInputStream("/Users/bmartins/Workspaces/spring-boot-sandbox/notifications/notifications-web/src/main/resources/application.yaml");
        AsyncInputStream asyncInputStream = new ReadableByteChannelAsyncInputStream(inputStream.getChannel());
        ReactiveGridFsResource resource = reactiveGridFsTemplate.store(asyncInputStream, "name.jpg")
                .flatMap( id -> reactiveGridFsTemplate.getResource("name.jpg"))
                .block();
        Mono.just(resource).subscribe(file -> {
            ToStringCreator creator = new ToStringCreator(file);
            creator.append("id", file.getId());
            creator.append("filename", file.getFilename());
            creator.append("length", file.getContentLength());
           System.out.println(creator.toString());
        });

        DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();

        toByteBuffer(resource.getAsyncInputStream(), dataBufferFactory).subscribe(s -> {
            System.out.print(new String(s.asByteBuffer().array()));
        });
    }
}
