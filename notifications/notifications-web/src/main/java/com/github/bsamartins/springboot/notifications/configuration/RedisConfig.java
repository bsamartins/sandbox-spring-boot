package com.github.bsamartins.springboot.notifications.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bsamartins.springboot.notifications.configuration.support.DockerContainerBean;
import com.github.bsamartins.springboot.notifications.messaging.Message;
import com.github.bsamartins.springboot.notifications.messaging.RedisMessagePublisher;
import com.github.bsamartins.springboot.notifications.messaging.SinkMessageListener;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.SocketUtils;
import org.testcontainers.shaded.com.google.common.collect.Lists;
import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.function.Function;

@Configuration
public class RedisConfig {

    public static final String MESSAGE_QUEUE = "message-queue";

    @Autowired
    ObjectMapper objectMapper;

    @Bean
    RedisMessageListenerContainer redisContainer() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory());
        return container;
    }

    @Bean
    public RedisMessagePublisher<com.github.bsamartins.springboot.notifications.domain.persistence.Message> redisMessagePublisher() {
        return new RedisMessagePublisher<>(jsonObjectRedisTemplate(), topic());
    }

    @Bean
    ChannelTopic topic() {
        return new ChannelTopic(MESSAGE_QUEUE);
    }

    @Bean
    public DockerContainerBean redisDockerContainer() {
        return new DockerContainerBean("redis:latest", redisPort(), 6379);
    }

    @Bean
    public int redisPort() {
        return SocketUtils.findAvailableTcpPort();
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setPort(redisPort());
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    public RedisTemplate<String, Object> jsonObjectRedisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        template.setValueSerializer(redisSerializer());

        return template;
    }

    @Bean
    public RedisSerializer<Object> redisSerializer() {
        ObjectMapper mapper = objectMapper.copy();
        mapper.enableDefaultTypingAsProperty(ObjectMapper.DefaultTyping.NON_FINAL, "_type");

        return new GenericJackson2JsonRedisSerializer(mapper);
    }

    @Bean
    Publisher<Message<com.github.bsamartins.springboot.notifications.domain.persistence.Message>> redisPublisher(RedisMessageListenerContainer redisMessageListenerContainer) {
        return registerTopicPublisher(redisMessageListenerContainer, new ChannelTopic(MESSAGE_QUEUE))
                .map(messageSerializerMapper());
    }

    private Flux<org.springframework.data.redis.connection.Message> registerTopicPublisher(RedisMessageListenerContainer redisMessageListenerContainer, Topic topic) {
        return registerTopicPublisher(redisMessageListenerContainer, Lists.newArrayList(topic));
    }

    private Flux<org.springframework.data.redis.connection.Message> registerTopicPublisher(RedisMessageListenerContainer redisMessageListenerContainer, Collection<? extends Topic> topics) {
        return Flux.create(sink -> {
            MessageListener listener = new SinkMessageListener(sink);
            redisMessageListenerContainer.addMessageListener(listener, topics);
        });
    }

    @Bean
    public <T> Function<org.springframework.data.redis.connection.Message, Message<T>> messageSerializerMapper() {
        return message -> {
            @SuppressWarnings("unchecked")
            T data = (T)this.redisSerializer().deserialize(message.getBody());

            Message<T> result = new Message<>(data);
            result.getHeaders().put("channel", new String(message.getChannel()));

            return result;
        };
    }

}