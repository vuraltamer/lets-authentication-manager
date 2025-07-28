package io.github.lets.apis.authentication.cache;

import io.github.lets.apis.authentication.google.response.GoogleCreateTokenResponse;
import io.github.lets.apis.authentication.properties.AuthenticationConfigurationProperties;
import io.github.lets.apis.authentication.cache.base.BaseCacheService;
import io.github.lets.apis.authentication.cache.model.TokenCache;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Set;

import static io.github.lets.apis.authentication.util.AuthenticationUtil.generateAccessToken;
import static io.github.lets.apis.authentication.util.AuthenticationUtil.generateRefreshToken;

public class TokenCacheService extends BaseCacheService {

    private static final String BASE_KEY = "auth:token:";

    private final AuthenticationConfigurationProperties configurationProperties;

    public TokenCacheService(RedisTemplate<String, Object> redisTemplate, AuthenticationConfigurationProperties configurationProperties) {
        super(redisTemplate);
        this.configurationProperties = configurationProperties;
    }

    public TokenCache create(String username, Set<String> roles) {
        TokenCache tokenCache = createTokenCache(username, roles, configurationProperties.getTtl().getAccessToken());
        create(tokenCache);
        return tokenCache;
    }

    public void create(TokenCache cache) {
        super.create(BASE_KEY + cache.getAccessToken(), cache.toJson(), Duration.ofSeconds(configurationProperties.getTtl().getRefreshToken()));
    }

    public void delete(String accessToken) {
        super.delete(BASE_KEY + accessToken);
    }

    public TokenCache get(String accessToken) {
        final String jsonValue = (String) super.get(BASE_KEY + accessToken);
        return TokenCache.toCache(jsonValue);
    }

    private static TokenCache createTokenCache(String username, Set<String> roles, Long accessTokenTtl) {
        return TokenCache.builder()
                .accessToken(generateAccessToken())
                .refreshToken(generateRefreshToken())
                .accessTokenExpireTime(accessTokenExpireTime(accessTokenTtl))
                .username(username)
                .roles(roles)
                .build();
    }

    private static int accessTokenExpireTime(Long accessTokenTtl) {
        LocalDateTime expireTime = LocalDateTime.now().plusSeconds(accessTokenTtl);
        return (int) expireTime.toEpochSecond(ZoneOffset.UTC);
    }

    public TokenCache create(String username, GoogleCreateTokenResponse tokenResponse) {
        TokenCache googleToken = createGoogleTokenCache(username, tokenResponse);
        create(googleToken);
        return googleToken;
    }

    private static TokenCache createGoogleTokenCache(String username, GoogleCreateTokenResponse tokenResponse) {
        return TokenCache.builder()
                .accessToken(tokenResponse.getAccessToken())
                .refreshToken(tokenResponse.getRefreshToken())
                .accessTokenExpireTime(accessTokenExpireTime(Long.valueOf(tokenResponse.getExpiresIn())))
                .roles(Set.of("GOOGLE_USER"))
                .username(username)
                .build();
    }
}