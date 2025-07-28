package io.github.lets.apis.authentication.manager.token;

import io.github.lets.apis.authentication.enums.TokenType;
import io.github.lets.apis.authentication.manager.token.base.TokenManager;
import io.github.lets.apis.authentication.properties.AuthenticationConfigurationProperties;
import io.github.lets.apis.authentication.util.JwtTokenGenerator;
import io.github.lets.apis.authentication.cache.model.TokenCache;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.rmi.AccessException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JwtTokenManager implements TokenManager {

    private final AuthenticationConfigurationProperties configurationProperties;

    public final SecretKey secretKey;

    public JwtTokenManager(AuthenticationConfigurationProperties configurationProperties) {
        this.configurationProperties = configurationProperties;
        AuthenticationConfigurationProperties.DefaultToken jwt = configurationProperties.getToken().getInit();
        this.secretKey = (jwt == null) ? null
                : Keys.hmacShaKeyFor(jwt.getJwtSecret().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public TokenCache create(String username, Set<String> roles) {
        String accessToken = JwtTokenGenerator.generateToken(username, roles, configurationProperties.getTtl().getAccessToken(), secretKey);
        String refreshToken = JwtTokenGenerator.generateToken(username, roles, configurationProperties.getTtl().getRefreshToken(), secretKey);
        return TokenCache.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .username(username)
                .roles(roles)
                .build();
    }

    @Override
    public TokenCache get(String token) throws AccessException {
        Claims claims = parseToken(token, secretKey);
        String username = claims.getSubject();
        List<String> roles = claims.get("roles", List.class);
        return TokenCache.builder()
                .accessToken(token)
                .username(username)
                .roles(new HashSet<>(roles))
                .build();
    }

    @Override
    public TokenCache refresh(String accessToken, String refreshToken) throws AccessException {
        Claims claims = parseToken(refreshToken, secretKey);
        String username = claims.getSubject();
        Set<String> roles = claims.get("roles", Set.class);
        String newAccessToken = JwtTokenGenerator.generateToken(username, roles, configurationProperties.getTtl().getAccessToken(), secretKey);
        String newRefreshToken = JwtTokenGenerator.generateToken(username, roles, configurationProperties.getTtl().getRefreshToken(), secretKey);
        return TokenCache.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .username(username)
                .roles(roles)
                .build();
    }

    public static Claims parseToken(String token, SecretKey secretKey) throws AccessException {
        try {
            Jws<Claims> parsed = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);

            return parsed.getBody();
        } catch (ExpiredJwtException e) {
            throw new AccessException("JwtTokenCacheManager::parseToken::token is expired");
        } catch (Exception e) {
            throw new AccessException("JwtTokenCacheManager::parseToken::exception::", e);
        }
    }

    @Override
    public void logout(String accessToken) {

    }

    @Override
    public TokenType type() {
        return TokenType.JWT;
    }
}