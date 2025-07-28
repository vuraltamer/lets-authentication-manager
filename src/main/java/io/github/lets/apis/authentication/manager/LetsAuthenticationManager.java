package io.github.lets.apis.authentication.manager;

import io.github.lets.apis.authentication.creator.LetsAuthenticationCreator;
import io.github.lets.apis.authentication.detail.manager.aop.LoadUserDetail;
import io.github.lets.apis.authentication.cache.model.TokenCache;
import io.github.lets.apis.authentication.security.model.LetsAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.rmi.AccessException;

/**
 * Manages authentication operations such as login, authenticate, refresh, and logout
 * for systems using either cache-based or JWT-based tokens.
 *
 * <p>This class acts as a central point for handling token-based authentication,
 * delegating token creation, validation, refresh, and logout operations.</p>
 */
@RequiredArgsConstructor
public class LetsAuthenticationManager {

    private final AuthenticationManager authenticationManager;
    private final LetsAuthenticationCreator authenticationCreator;

    /**
     * Authenticates the user with username and password, and generates a token.
     * Supports both cacheable and JWT-based authentication.
     *
     * @param username the username (typically email or user ID)
     * @param password the raw password for login
     * @return a TokenCache containing access and refresh tokens
     */
    @LoadUserDetail
    public TokenCache login(String username, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        return authenticationCreator.createToken(username, authenticate.getAuthorities());
    }

    /**
     * Authenticates a user using the access token.
     * If valid, sets the authenticated user into the Spring Security context.
     *
     * @param accessToken the token to validate and authenticate
     * @throws AccessException if the token is invalid or expired
     */
    public void authenticate(String accessToken) throws AccessException {
        LetsAuthentication authentication = authenticationCreator.authenticate(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    /**
     * Refreshes the access token using the given refresh token.
     * Applies to both JWT and cacheable token strategies.
     *
     * @param accessToken the expired or soon-to-expire access token
     * @param refreshToken the refresh token used to generate a new access token
     * @return a new TokenCache with updated tokens
     * @throws AccessException if the refresh token is invalid
     */
    @LoadUserDetail
    public TokenCache refresh(String accessToken, String refreshToken) throws AccessException {
        return authenticationCreator.refreshToken(accessToken, refreshToken);
    }

    /**
     * Logs out the user by invalidating the access token.
     * This operation applies only to cacheable token-based authentication.
     *
     * @param accessToken the token to invalidate
     */
    public void logout(String accessToken) {
        authenticationCreator.logout(accessToken);
    }
}