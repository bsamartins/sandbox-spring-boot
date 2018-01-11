package com.github.bsamartins.springboot.notifications.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
    
    @Bean
    public InitializerBean initializerBean() {
        return new InitializerBean();
    }


}
