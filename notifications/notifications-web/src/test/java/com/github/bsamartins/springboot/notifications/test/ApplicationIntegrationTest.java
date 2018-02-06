package com.github.bsamartins.springboot.notifications.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bsamartins.springboot.notifications.domain.persistence.User;
import com.github.bsamartins.springboot.notifications.repository.UserRepository;
import com.github.bsamartins.springboot.notifications.security.jwt.JWTAuthenticationService;
import org.junit.Before;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

@ExtendWith(SpringExtension.class)
@EnableReactiveMongoRepositories
@ComponentScan("com.github.bsamartins")
@WebFluxTest(excludeAutoConfiguration = { MongoAutoConfiguration.class, MongoDataAutoConfiguration.class, MongoReactiveDataAutoConfiguration.class })
public abstract class ApplicationIntegrationTest {

    private static final String TEST_USERNAME = "john.doe";
    private static final String TEST_PASSWORD = "password";

    @Autowired
    private JWTAuthenticationService jwtAuthenticationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Configuration
    public static class TestConfig {
        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }

    @Before
    final public void init() {}

    protected Consumer<HttpHeaders> withUser() {
        return httpHeaders -> {
            JWTAuthenticationService.JwtToken token = userRepository.findByUsername(TEST_USERNAME)
                    .switchIfEmpty(createUser(TEST_USERNAME, TEST_PASSWORD))
                    .map(user -> new UsernamePasswordAuthenticationToken(TEST_USERNAME, TEST_PASSWORD))
                    .flatMap(jwtAuthenticationService::reactiveAuthenticate)
                    .block();
            httpHeaders.set(HttpHeaders.AUTHORIZATION, token.getType() + " " + token.getToken());
        };

    }

    private Mono<User> createUser(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        return userRepository.save(user);
    }
}
