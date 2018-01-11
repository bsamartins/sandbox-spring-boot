package com.github.bsamartins.springboot.notifications.security;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class NoPasswordReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    private final ReactiveUserDetailsService repository;

    public NoPasswordReactiveAuthenticationManager(ReactiveUserDetailsService reactiveUserDetailsService) {
        Assert.notNull(reactiveUserDetailsService, "userDetailsRepository cannot be null");
        this.repository = reactiveUserDetailsService;

    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        final String username = authentication.getName();
        return this.repository.findByUsername(username)
                .publishOn(Schedulers.parallel())
                .switchIfEmpty(  Mono.error(new BadCredentialsException("Invalid Credentials")) )
                .map( u -> new UsernamePasswordAuthenticationToken(u, u.getPassword(), u.getAuthorities()) );
    }

}
