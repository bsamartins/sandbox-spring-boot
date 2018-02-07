package com.github.bsamartins.springboot.notifications.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bsamartins.springboot.notifications.configuration.WebFluxSecurityConfig;
import com.github.bsamartins.springboot.notifications.domain.persistence.User;
import com.github.bsamartins.springboot.notifications.repository.UserRepository;
import com.github.bsamartins.springboot.notifications.security.jwt.JWTAuthenticationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

@ExtendWith(SpringExtension.class)
@EnableReactiveMongoRepositories
@ComponentScan("com.github.bsamartins")
@WebFluxTest(excludeAutoConfiguration = {
        MongoAutoConfiguration.class,
        MongoDataAutoConfiguration.class,
        MongoReactiveDataAutoConfiguration.class })
@ContextConfiguration(classes = {
        ApplicationIntegrationTest.TestConfig.class,
        ApplicationIntegrationTest.TestSecurityConfig.class
})
public abstract class ApplicationIntegrationTest {

    private static final String TEST_USERNAME = "john.doe";
    private static final String TEST_PASSWORD = "password";

    @Autowired
    private JWTAuthenticationService jwtAuthenticationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

    private User defaultUser;

    @BeforeEach
    final public void init() {
        defaultUser = createUser(TEST_USERNAME, TEST_PASSWORD).block();
    }

    protected User getDefaultUser() {
        return defaultUser;
    }

    protected Consumer<HttpHeaders> withUser() {
        return this.withUser(getDefaultUser());
    }

    protected Consumer<HttpHeaders> withUser(User user) {
        return httpHeaders -> {
            JWTAuthenticationService.JwtToken token = Mono.just(user)
                    .map(u -> new UsernamePasswordAuthenticationToken(u.getUsername(), u.getPassword()))
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

    @AfterEach
    public void tearDown() {
        Mono.from(this.reactiveMongoTemplate.getMongoDatabase().drop())
                .subscribe();
    }

    @Configuration
    public static class TestConfig {
        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }

    @Configuration
    public static class TestSecurityConfig extends WebFluxSecurityConfig {
        @Bean
        @Override
        public PasswordEncoder passwordEncoder() {
            return NoOpPasswordEncoder.getInstance();
        }
    }
}
