package com.github.bsamartins.springboot.notifications.security.jwt;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.util.Assert;

public class JWTAuthenticationToken extends AbstractAuthenticationToken {

    private String principal;

    public JWTAuthenticationToken(String username) {
        super(null);
        Assert.notNull(username, "Username expected");
        this.principal = username;
    }

    @Override
    public Object getCredentials() {
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }
}
