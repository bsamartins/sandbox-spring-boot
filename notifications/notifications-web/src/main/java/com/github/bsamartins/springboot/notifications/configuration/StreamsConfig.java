package com.github.bsamartins.springboot.notifications.configuration;

import com.github.bsamartins.springboot.notifications.messaging.ChatStreams;
import org.springframework.cloud.stream.annotation.EnableBinding;

@EnableBinding(ChatStreams.class)
public class StreamsConfig {
}