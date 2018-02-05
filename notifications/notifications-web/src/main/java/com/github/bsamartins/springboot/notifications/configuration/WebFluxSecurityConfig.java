package com.github.bsamartins.springboot.notifications.configuration;

import com.github.bsamartins.springboot.notifications.security.HttpStatusResponseAuthenticationEntryPoint;
import com.github.bsamartins.springboot.notifications.security.NoPasswordReactiveAuthenticationManager;
import com.github.bsamartins.springboot.notifications.security.ReactiveUserDetailsServiceImpl;
import com.github.bsamartins.springboot.notifications.security.jwt.JWTAuthenticationConverter;
import com.github.bsamartins.springboot.notifications.security.jwt.JWTAuthenticationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationEntryPointFailureHandler;

import static org.springframework.security.config.web.server.SecurityWebFiltersOrder.AUTHENTICATION;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class WebFluxSecurityConfig {

    @Bean
    public ReactiveUserDetailsService reactiveUserDetailsService() {
        return new ReactiveUserDetailsServiceImpl();
    }

    @Primary
    @Bean
    protected ReactiveAuthenticationManager authenticationManager(ReactiveUserDetailsService reactiveUserDetailsService) {
        UserDetailsRepositoryReactiveAuthenticationManager authenticationManager = new UserDetailsRepositoryReactiveAuthenticationManager(reactiveUserDetailsService);
        authenticationManager.setPasswordEncoder(passwordEncoder());
        return authenticationManager;
    }

    @Bean NoPasswordReactiveAuthenticationManager noPasswordAuthenticationManager(ReactiveUserDetailsService reactiveUserDetailsService) {
        return new NoPasswordReactiveAuthenticationManager(reactiveUserDetailsService);
    }

    @Bean
    SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http, AuthenticationWebFilter jwtAuthenticationWebFilter, ServerAuthenticationEntryPoint serverAuthenticationEntryPoint) {
        return http
                // Demonstrate that method security works
                // Best practice to use both for defense in depth
                .addFilterAt(jwtAuthenticationWebFilter, AUTHENTICATION)
                .csrf().disable().authorizeExchange()
                .pathMatchers(HttpMethod.GET, "/api/files/*").permitAll()
                .pathMatchers("/api/**").authenticated()
                .pathMatchers("/**").permitAll()
                .and()
                .exceptionHandling().authenticationEntryPoint(serverAuthenticationEntryPoint)
                .and()
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationWebFilter jwtAuthenticationWebFilter(NoPasswordReactiveAuthenticationManager noPasswordReactiveAuthenticationManager,
                                                              JWTAuthenticationConverter jwtAuthenticationConverter,
                                                              ServerAuthenticationEntryPoint serverAuthenticationEntryPoint) {
        AuthenticationWebFilter authenticationWebFilter = new AuthenticationWebFilter(noPasswordReactiveAuthenticationManager);
        authenticationWebFilter.setAuthenticationConverter(jwtAuthenticationConverter);
        authenticationWebFilter.setAuthenticationFailureHandler(new ServerAuthenticationEntryPointFailureHandler(serverAuthenticationEntryPoint));
        return authenticationWebFilter;
    }

    @Bean
    public ServerAuthenticationEntryPoint serverAuthenticationEntryPoint() {
        return new HttpStatusResponseAuthenticationEntryPoint(HttpStatus.UNAUTHORIZED);
    }

    @Bean
    public JWTAuthenticationConverter jwtAuthenticationConverter(JWTAuthenticationService jwtAuthenticationService) {
        return new JWTAuthenticationConverter(jwtAuthenticationService);
    }
}