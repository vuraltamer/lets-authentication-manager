package io.github.lets.apis.authentication;

import io.github.lets.apis.authentication.creator.LetsAuthenticationCreator;
import io.github.lets.apis.authentication.detail.fetch.GoogleAuthenticationUserFetchService;
import io.github.lets.apis.authentication.google.provider.GoogleAuthenticationProvider;
import io.github.lets.apis.authentication.google.service.GoogleAuthenticationService;
import io.github.lets.apis.authentication.manager.GoogleAuthenticationManager;
import io.github.lets.apis.authentication.manager.LetsAuthenticationManager;
import io.github.lets.apis.authentication.manager.token.*;
import io.github.lets.apis.authentication.manager.token.base.TokenManager;
import io.github.lets.apis.authentication.manager.user.UserCacheManager;
import io.github.lets.apis.authentication.properties.AuthenticationConfigurationProperties;
import io.github.lets.apis.authentication.detail.fetch.AuthenticationUserFetchService;
import io.github.lets.apis.authentication.detail.loader.AuthenticationUserLoadService;
import io.github.lets.apis.authentication.detail.manager.UserDetailManager;
import io.github.lets.apis.authentication.detail.manager.aop.UserDetailManagerAspect;
import io.github.lets.apis.authentication.cache.TokenCacheService;
import io.github.lets.apis.authentication.cache.UserCacheService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Auto-configuration class for the Lets Authentication Module.
 *
 * <p>This configuration sets up authentication support using either:
 * <ul>
 *     <li><b>Cacheable tokens (Redis-based)</b> or</li>
 *     <li><b>JWT tokens (stateless)</b></li>
 * </ul>
 * depending on the value of the configuration property
 * <code>lets.apis.authentication.token.init.type</code>.
 *
 * <p><b>Token type conditions:</b>
 * <ul>
 *     <li>If <code>type=CACHEABLE</code>, beans for {@link CacheableTokenManager} and Redis-backed token services will be created.</li>
 *     <li>If <code>type=JWT</code>, beans for {@link JwtTokenManager} will be created.</li>
 *     <li>The two modes are mutually exclusive — only one will be active based on the configuration.</li>
 * </ul>
 *
 * <p><b>Google Authentication:</b>
 * <ul>
 *     <li>Google login support will only be enabled if <code>lets.apis.authentication.token.google.enabled=true</code>.</li>
 * </ul>
 *
 * <p><b>Redis Dependency:</b>
 * <ul>
 *     <li>This module <b>requires</b> a working Redis connection, as it depends on {@link RedisConnectionFactory} for session and token caching, even when JWT is selected.</li>
 * </ul>
 *
 * <p>Only activates when required classes like {@link AuthenticationManager},
 * {@link RedisConnectionFactory}, and user-fetch service interfaces are present in the classpath.
 */
@AutoConfiguration
@ConditionalOnClass({RedisConnectionFactory.class, AuthenticationManager.class, GoogleAuthenticationUserFetchService.class, AuthenticationUserFetchService.class})
@EnableConfigurationProperties(AuthenticationConfigurationProperties.class)
public class LetsAuthenticationManagerAutoConfiguration {

    @Bean
    public LetsAuthenticationManager redisAuthenticationManager(LetsAuthenticationCreator letsAuthenticationCreator,
                                                                AuthenticationManager authenticationManager) {
        return new LetsAuthenticationManager(authenticationManager, letsAuthenticationCreator);
    }

    @Bean
    @ConditionalOnProperty(prefix = "lets.apis.authentication.token.google", name = "enabled", havingValue = "true")
    public GoogleAuthenticationManager googleAuthenticationManager(AuthenticationManager authenticationManager,
                                                                   GoogleAuthenticationService authenticationService,
                                                                   GoogleAuthenticationUserFetchService googleAuthenticationUserFetchService,
                                                                   UserCacheManager userCacheManager,
                                                                   GoogleTokenManager googleTokenManager) {
        return new GoogleAuthenticationManager(authenticationManager,
                authenticationService,
                googleAuthenticationUserFetchService,
                userCacheManager,
                googleTokenManager);
    }

    @Bean
    @ConditionalOnProperty(prefix = "lets.apis.authentication.token.google", name = "enabled", havingValue = "true")
    public GoogleAuthenticationProvider googleAuthenticationProvider(GoogleAuthenticationService googleAuthenticationService) {
        return new GoogleAuthenticationProvider(googleAuthenticationService);
    }

    @Bean
    @ConditionalOnProperty(prefix = "lets.apis.authentication.token.google", name = "enabled", havingValue = "true")
    public GoogleTokenManager googleTokenManager(TokenCacheService tokenCacheService) {
        return new GoogleTokenManager(tokenCacheService);
    }

    @Bean
    public LetsAuthenticationCreator redisAuthenticationCreator(UserCacheManager userCacheManager,
                                                                AuthenticationConfigurationProperties properties,
                                                                TokenStrategyResolver tokenStrategyResolver) {
        return new LetsAuthenticationCreator(userCacheManager,
                tokenStrategyResolver,
                properties);
    }

    @Bean
    public TokenStrategyResolver tokenStrategyResolver(Optional<CacheableTokenManager> cacheableTokenManager,
                                                       Optional<JwtTokenManager> jwtTokenManager) {
        List<TokenManager> tokenManagers = new ArrayList<>();
        cacheableTokenManager.ifPresent(tokenManagers::add);
        jwtTokenManager.ifPresent(tokenManagers::add);
        return new TokenStrategyResolver(tokenManagers);
    }

    @Bean
    @ConditionalOnProperty(prefix = "lets.apis.authentication.token.init", name = "type", havingValue = "CACHEABLE")
    public CacheableTokenManager cacheableTokenManager(TokenCacheService tokenCacheService) {
        return new CacheableTokenManager(tokenCacheService);
    }

    @Bean
    @ConditionalOnProperty(prefix = "lets.apis.authentication.token.init", name = "type", havingValue = "JWT")
    public JwtTokenManager jwtTokenManager(AuthenticationConfigurationProperties authenticationConfigurationProperties) {
        return new JwtTokenManager(authenticationConfigurationProperties);
    }

    @Bean
    public UserCacheManager userCacheManager(UserCacheService userCacheService) {
        return new UserCacheManager(userCacheService);
    }

    @Bean
    public UserDetailManagerAspect userDetailManagerAspect(UserDetailManager userDetailManager) {
        return new UserDetailManagerAspect(userDetailManager);
    }

    @Bean
    public UserDetailManager userDetailManager(AuthenticationUserFetchService userDetailFetchService, AuthenticationUserLoadService userDetailLoadService) {
        return new UserDetailManager(userDetailLoadService, userDetailFetchService);
    }

    @Bean
    public AuthenticationUserLoadService userDetailLoadService(UserCacheService userCacheService) {
        return new AuthenticationUserLoadService(userCacheService);
    }

    @Bean
    @ConditionalOnProperty(prefix = "lets.apis.authentication.token.google", name = "enabled", havingValue = "true")
    public GoogleAuthenticationService googleAuthenticationService(AuthenticationConfigurationProperties properties,
                                                                   @Qualifier("letsAuthenticationRestTemplate") RestTemplate restTemplate) {
        return new GoogleAuthenticationService(properties, restTemplate);
    }

    @Bean
    public TokenCacheService tokenCacheService(@Qualifier("letsAuthenticationRedisTemplate") RedisTemplate<String, Object> redisTemplate,
                                               AuthenticationConfigurationProperties authenticationConfigurationProperties) {
        return new TokenCacheService(redisTemplate, authenticationConfigurationProperties);
    }

    @Bean
    public UserCacheService userCacheService(@Qualifier("letsAuthenticationRedisTemplate") RedisTemplate<String, Object> redisTemplate) {
        return new UserCacheService(redisTemplate);
    }

    @Bean(value = "letsAuthenticationRedisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        return redisTemplate;
    }

    @Bean("letsAuthenticationRestTemplate")
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}