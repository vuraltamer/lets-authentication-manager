package io.github.lets.apis.authentication.creator;

import io.github.lets.apis.authentication.enums.TokenType;
import io.github.lets.apis.authentication.manager.token.TokenStrategyResolver;
import io.github.lets.apis.authentication.manager.token.base.TokenManager;
import io.github.lets.apis.authentication.manager.user.UserCacheManager;
import io.github.lets.apis.authentication.cache.model.TokenCache;
import io.github.lets.apis.authentication.cache.model.UserCache;
import io.github.lets.apis.authentication.properties.AuthenticationConfigurationProperties;
import io.github.lets.apis.authentication.security.model.LetsAuthentication;
import io.github.lets.apis.authentication.util.AuthenticationUtil;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.ObjectUtils;

import java.rmi.AccessException;
import java.util.Collection;

public class LetsAuthenticationCreator {

    private final UserCacheManager userCacheManager;
    private final TokenStrategyResolver tokenStrategyResolver;
    private final AuthenticationConfigurationProperties properties;

    public LetsAuthenticationCreator(UserCacheManager userCacheManager,
                                     TokenStrategyResolver tokenStrategyResolver, AuthenticationConfigurationProperties properties) {
        this.userCacheManager = userCacheManager;
        this.tokenStrategyResolver = tokenStrategyResolver;
        this.properties = properties;
    }

    public TokenCache createToken(String username, Collection<? extends GrantedAuthority> authorities) {
        UserCache userCache = userCacheManager.create(username, authorities);
        TokenCache tokenCache = tokenManager().create(username, userCache.getRoles());
        return tokenCache;
    }

    public LetsAuthentication authenticate(String accessToken) throws AccessException {
        TokenCache tokenCache = tokenManager().get(accessToken);
        AuthenticationUtil.authenticate(tokenCache);
        UserCache userCache = userCacheManager.get(tokenCache.getUsername());
        Object principal = !ObjectUtils.isEmpty(userCache.getDetail()) ? userCache.getDetail().getPrincipal() : null;
        return new LetsAuthentication(accessToken, principal, userCache.getRoles(), true);
    }

    public TokenCache refreshToken(String accessToken, String refreshToken) throws AccessException {
        return tokenManager().refresh(accessToken, refreshToken);
    }

    private TokenManager tokenManager() {
        TokenType type = properties.getToken().getInit().getType();
        return tokenStrategyResolver.resolve(type);
    }

    public void logout(String accessToken) {
        tokenManager().logout(accessToken);
    }
}