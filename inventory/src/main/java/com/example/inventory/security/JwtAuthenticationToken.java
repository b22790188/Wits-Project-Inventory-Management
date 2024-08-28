package com.example.inventory.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import com.example.inventory.dto.UserDto;

import java.util.Collection;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final UserDto principal;
    private final String token;

    public JwtAuthenticationToken(UserDto principal, String token, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.token = token;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
