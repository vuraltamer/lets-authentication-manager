package io.github.lets.apis.authentication.security.model;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Set;

public class LetsAuthentication implements Authentication {
    private final String token;
    private final Object principal;
    private final boolean authenticated;
    private final Collection<? extends GrantedAuthority> authorities;

    public LetsAuthentication(String token, Object principal, Set<String> roles, boolean authenticated) {
        this.token = token;
        this.principal = principal;
        this.authenticated = authenticated;
        this.authorities = Authority.getSimpleGrantedAuthorities(roles);
    }

    public LetsAuthentication(String token, Object principal, Collection<? extends GrantedAuthority> authorities, boolean authenticated) {
        this.token = token;
        this.principal = principal;
        this.authenticated = authenticated;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        throw new UnsupportedOperationException("Authentication state is immutable");
    }

    @Override
    public String getName() {
        return null;
    }

    public String getToken() {
        return this.token;
    }
}