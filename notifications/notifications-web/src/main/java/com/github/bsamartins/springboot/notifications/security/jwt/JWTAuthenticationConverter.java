package com.github.bsamartins.springboot.notifications.security.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.function.Function;

import static com.github.bsamartins.springboot.notifications.security.SecurityConstants.TOKEN_PREFIX;

public class JWTAuthenticationConverter implements Function<ServerWebExchange, Mono<Authentication>> {

    private static Logger LOGGER = LoggerFactory.getLogger(JWTAuthenticationConverter.class);

    private JWTAuthenticationService jwtAuthenticationService;

    public JWTAuthenticationConverter(JWTAuthenticationService jwtAuthenticationService) {
        this.jwtAuthenticationService = jwtAuthenticationService;
    }

    @Override
    public Mono<Authentication> apply(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();

        String authorization = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if(authorization == null) {
            return Mono.empty();
        }

        String credentials = authorization.length() <= TOKEN_PREFIX.length() ?
                "" : authorization.substring(TOKEN_PREFIX.length(), authorization.length());
        try {
            String username = jwtAuthenticationService.parseToken(credentials)
                    .getBody()
                    .getSubject();

            return Mono.just(new JWTAuthenticationToken(username));
        } catch (BadCredentialsException bce) {
            LOGGER.warn("Unable to parse JWT", bce);
            return Mono.empty();
        }
    }
}
