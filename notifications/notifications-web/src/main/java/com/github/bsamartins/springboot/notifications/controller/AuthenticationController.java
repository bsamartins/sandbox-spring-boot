package com.github.bsamartins.springboot.notifications.controller;

import com.github.bsamartins.springboot.notifications.domain.Credentials;
import com.github.bsamartins.springboot.notifications.security.jwt.JWTAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private JWTAuthenticationService jwtAuthenticationService;

    @RequestMapping(path = "/token", method = RequestMethod.POST)
    public Mono<JWTAuthenticationService.JwtToken> issueToken(@RequestBody Credentials credentials) throws Exception {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(credentials.getUsername(), credentials.getPassword());
        return jwtAuthenticationService.reactiveAuthenticate(usernamePasswordAuthenticationToken);
    }

}
