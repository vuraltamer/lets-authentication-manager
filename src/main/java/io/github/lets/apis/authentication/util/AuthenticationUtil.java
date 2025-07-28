package io.github.lets.apis.authentication.util;

import io.github.lets.apis.authentication.cache.model.TokenCache;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.rmi.AccessException;
import java.time.Instant;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthenticationUtil {

    public static String generateAccessToken() {
        return "acc-{TOKEN}"
                .replace("{TOKEN}", DefaultTokenGenerator.createToken());
    }

    public static String generateRefreshToken() {
        return "ref-{TOKEN}"
                .replace("{TOKEN}", DefaultTokenGenerator.createToken());
    }

    public static String getAccessToken(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        return authHeader != null && authHeader.startsWith("Bearer ")
                ? authHeader.substring(7) : null;
    }

    public static void authenticate(TokenCache tokenCache) throws AccessException {
        if (tokenCache == null) {
            throw new AccessException("Access token is not valid.");
        }
        long currentEpochSeconds = Instant.now().getEpochSecond();
        if (tokenCache.getAccessTokenExpireTime() <= currentEpochSeconds) {
            throw new AccessException("Access token is not valid.");
        }
    }
}