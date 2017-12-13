package com.github.bsamartins.spring.boot.beat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bsamartins.spring.boot.beat.suppliers.actuator.MetricsSupplier;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class BeatConfiguration {

    @Bean
    public ScheduledExecutorService beatScheduledExecutorService() {
        return Executors.newScheduledThreadPool(4);
    }

    @Bean
    public BeatAgent actuatorBeatAgent(BeatConfigurer configurer, ObjectMapper objectMapper) {
        BeatAgent agent = new BeatAgent(beatScheduledExecutorService(), objectMapper);
        agent.setInterval(configurer.getInterval());
        agent.setSuppliers(configurer.getSuppliers());
        agent.setHandlers(configurer.getHandlers());
        return agent;
    }

    @Bean
    @ConditionalOnMissingBean(BeatConfigurer.class)
    public BeatConfigurer actuatorBeatConfigurer(MetricsSupplier metricsSupplier) {
        BeatConfigurer configurer = new BeatConfigurer();
        configurer.setInterval(Duration.ofSeconds(1));
        configurer.registerSupplier(metricsSupplier);
        return configurer;
    }
}
