package com.github.bsamartins.springboot.notifications.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import static com.github.bsamartins.springboot.notifications.security.SecurityConstants.TOKEN_PREFIX;

@Service
public class JWTAuthenticationService {

    @Value("app.security.jwt.secret")
    private String jwtSecret;

    @Value("#{T(java.time.Duration).parse('${app.security.jwt.duration}')}")
    private Duration jwtDuration;

    @Autowired
    private ReactiveAuthenticationManager reactiveAuthenticationManager;

    @Autowired
    private ReactiveUserDetailsService userDetailsService;

    public Authentication authenticate(String token) throws BadCredentialsException {
        if(token == null) {
            return null;
        }

        Jws<Claims> jws = parseToken(token);
        return Optional.ofNullable(jws.getBody().getSubject())
                .map(username -> userDetailsService.findByUsername(username).block())
                .map(userDetails -> new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, new ArrayList<>()))
                .orElse(null);
    }

    private JwtToken createToken(Authentication auth) {
        String token = Jwts.builder()
                .setSubject(((User) auth.getPrincipal()).getUsername())
                .setExpiration(Date.from(LocalDateTime.now().plus(jwtDuration).toInstant(ZoneOffset.UTC)))
                .signWith(SignatureAlgorithm.HS512, jwtSecret.getBytes())
                .compact();
        return new JwtToken(TOKEN_PREFIX,  token);
    }

    public Mono<JwtToken> reactiveAuthenticate(Authentication authentication) {
        return this.reactiveAuthenticationManager.authenticate(authentication)
                .map(this::createToken);
    }

    public Jws<Claims> parseToken(String token) throws BadCredentialsException {
        try {
            return Jwts.parser()
                    .setSigningKey(jwtSecret.getBytes())
                    .parseClaimsJws(token);

        } catch(Exception e) {
            throw new BadCredentialsException("Error parsing token", e);
        }
    }

    public static class JwtToken {

        private String type;
        private String token;

        public JwtToken(String type, String token) {
            this.type = type;
            this.token = token;
        }

        public String getType() {
            return this.type;
        }

        public String getToken() {
            return this.token;
        }
    }
}
