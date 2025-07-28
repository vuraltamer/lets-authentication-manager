package io.github.lets.apis.authentication.manager.token;

import io.github.lets.apis.authentication.cache.TokenCacheService;
import io.github.lets.apis.authentication.cache.model.TokenCache;
import io.github.lets.apis.authentication.enums.TokenType;
import io.github.lets.apis.authentication.google.response.GoogleCreateTokenResponse;
import io.github.lets.apis.authentication.google.response.GoogleRefreshTokenResponse;

import java.util.Set;

public class GoogleTokenManager extends CacheableTokenManager {

    public GoogleTokenManager(TokenCacheService tokenCacheService) {
        super(tokenCacheService);
    }

    public TokenCache get(String accessToken) {
        return tokenCacheService.get(accessToken);
    }

    public TokenCache create(String username, GoogleCreateTokenResponse tokenResponse) {
        return tokenCacheService.create(username, tokenResponse);
    }

    public TokenCache refresh(String username, String currentAccessToken, String refreshToken, GoogleRefreshTokenResponse tokenResponse) {
        TokenCache tokenCache = createGoogleUser(username, refreshToken, tokenResponse);
        tokenCacheService.create(tokenCache);
        tokenCacheService.delete(currentAccessToken);
        return tokenCache;
    }

    private static TokenCache createGoogleUser(String username, String refreshToken, GoogleRefreshTokenResponse tokenResponse) {
        return TokenCache.builder()
                .accessToken(tokenResponse.getAccessToken())
                .refreshToken(refreshToken)
                .accessTokenExpireTime(tokenResponse.getExpiresIn())
                .roles(Set.of("GOOGLE_USER"))
                .username(username)
                .build();
    }

    @Override
    public TokenType type() {
        return TokenType.GOOGLE;
    }

}