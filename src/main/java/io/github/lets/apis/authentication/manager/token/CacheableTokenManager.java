package io.github.lets.apis.authentication.manager.token;

import io.github.lets.apis.authentication.enums.TokenType;
import io.github.lets.apis.authentication.manager.token.base.TokenManager;
import io.github.lets.apis.authentication.cache.TokenCacheService;
import io.github.lets.apis.authentication.cache.model.TokenCache;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.ObjectUtils;

import java.util.Set;

@RequiredArgsConstructor
public class CacheableTokenManager implements TokenManager {

    protected final TokenCacheService tokenCacheService;

    @Override
    public TokenCache get(String accessToken) {
        return tokenCacheService.get(accessToken);
    }

    @Override
    public TokenCache create(String username, Set<String> roles) {
        return tokenCacheService.create(username, roles);
    }

    @Override
    public TokenCache refresh(String accessToken, String refreshToken) {
        TokenCache currentToken = getCurrentToken(accessToken, refreshToken);
        tokenCacheService.delete(accessToken);
        TokenCache newToken = create(currentToken.getUsername(), currentToken.getRoles());
        return newToken;
    }


    @Override
    public void logout(String accessToken) {
        tokenCacheService.delete(accessToken);
    }

    private TokenCache getCurrentToken(String accessToken, String refreshToken) {
        final TokenCache tokenCache = tokenCacheService.get(accessToken);
        if (ObjectUtils.isEmpty(tokenCache) || !refreshToken.equals(tokenCache.getRefreshToken())) {
            throw new AccessDeniedException("Refresh token is not valid!");
        }
        return tokenCache;
    }

    @Override
    public TokenType type() {
        return TokenType.CACHEABLE;
    }
}