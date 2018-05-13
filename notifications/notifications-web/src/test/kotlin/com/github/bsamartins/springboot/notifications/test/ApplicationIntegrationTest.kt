package com.github.bsamartins.springboot.notifications.test

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.bsamartins.springboot.notifications.configuration.WebFluxSecurityConfig
import com.github.bsamartins.springboot.notifications.domain.persistence.User
import com.github.bsamartins.springboot.notifications.repository.UserRepository
import com.github.bsamartins.springboot.notifications.security.jwt.JWTAuthenticationService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import reactor.core.publisher.Mono
import java.util.function.Consumer

@ExtendWith(SpringExtension::class)
@EnableReactiveMongoRepositories()
@ComponentScan("com.github.bsamartins")
@WebFluxTest(excludeAutoConfiguration = [
        MongoAutoConfiguration::class,
        MongoDataAutoConfiguration::class,
        MongoReactiveDataAutoConfiguration::class ])
@ContextConfiguration(classes = [
        MongoTestConfig::class,
        TestConfig::class,
        TestSecurityConfig::class
])
abstract class ApplicationIntegrationTest {

    companion object {
        private const val TEST_USERNAME: String = "john.doe"
        private const val TEST_PASSWORD: String = "password"
    }


    @Autowired
    private lateinit var jwtAuthenticationService: JWTAuthenticationService

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var reactiveMongoTemplate: ReactiveMongoTemplate

    private lateinit var defaultUser: User

    @BeforeEach
    fun integrationTestSetup() {
        defaultUser = createUser(TEST_USERNAME, TEST_PASSWORD).block()
    }

    protected fun getDefaultUser(): User {
        return defaultUser
    }

    protected fun withUser(): Consumer<HttpHeaders> {
        return this.withUser(getDefaultUser());
    }

    protected fun withUser(user: User): Consumer<HttpHeaders> {
        return Consumer{ httpHeaders ->
            val token: JWTAuthenticationService.JwtToken = Mono.just(user)
                    .map{ u -> UsernamePasswordAuthenticationToken(u.username, u.password) }
                    .flatMap(jwtAuthenticationService::reactiveAuthenticate)
                    .block()
            httpHeaders.set(HttpHeaders.AUTHORIZATION, "${token.type} ${token.token}")
        }
    }

    private fun createUser(username: String, password: String): Mono<User>  {
        val user = User()
        user.username = username
        user.password = passwordEncoder.encode(password)
        return userRepository.save(user)
    }

    @AfterEach
    fun integrationTestTearDown() {
        Mono.from(this.reactiveMongoTemplate.mongoDatabase.drop())
                .subscribe();
    }
}

class TestConfig {
    @Bean
    fun objectMapper(): ObjectMapper {
        return ObjectMapper()
    }
}

class TestSecurityConfig: WebFluxSecurityConfig() {
    @Bean
    @Override
    override fun passwordEncoder(): PasswordEncoder {
        return NoOpPasswordEncoder.getInstance();
    }
}