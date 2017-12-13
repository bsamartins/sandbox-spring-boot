package com.github.bsamartins.springboot.notifications.configuration;

import com.github.bsamartins.springboot.notifications.domain.Event;
import org.reactivestreams.Publisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.jms.dsl.Jms;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.messaging.Message;

import javax.jms.ConnectionFactory;

import static com.github.bsamartins.springboot.notifications.configuration.ActiveMQConfig.ORDER_QUEUE;

@Configuration
public class IntegrationFlowsConfig {

    @Bean
    public Publisher<Message<Event>> jmsEventInbound(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        return IntegrationFlows
                .from(Jms.messageDrivenChannelAdapter(connectionFactory)
                        .destination(ORDER_QUEUE)
                        .jmsMessageConverter(messageConverter)
                        .get())
                .channel(MessageChannels.queue())
                .log(LoggingHandler.Level.DEBUG)
                .log()
                .toReactivePublisher();
    }
}
