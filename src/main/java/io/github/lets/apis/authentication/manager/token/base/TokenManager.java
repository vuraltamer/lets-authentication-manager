package io.github.lets.apis.authentication.manager.token.base;

import io.github.lets.apis.authentication.cache.model.TokenCache;
import io.github.lets.apis.authentication.enums.TokenType;

import java.rmi.AccessException;
import java.util.Set;

public interface TokenManager {
    TokenCache create(String username, Set<String> roles);
    TokenCache get(String token) throws AccessException;
    TokenCache refresh(String accessToken, String refreshToken) throws AccessException;
    void logout(String accessToken);

    TokenType type();
}