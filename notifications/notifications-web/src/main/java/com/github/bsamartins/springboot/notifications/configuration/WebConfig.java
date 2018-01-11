package com.github.bsamartins.springboot.notifications.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
@EnableWebFlux
public class WebConfig implements WebFluxConfigurer {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebFluxConfigurer.class);

    @Value("${app.ui.resources-location:classpath:/notifications-ui/}")
    private String resourcesLocation;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        LOGGER.debug("Mapping resources from {}", resourcesLocation);
        registry.addResourceHandler("/**")
                .addResourceLocations(resourcesLocation);
    }

}