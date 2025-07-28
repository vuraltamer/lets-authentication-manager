package io.github.lets.apis.authentication.manager;

import io.github.lets.apis.authentication.cache.model.TokenCache;
import io.github.lets.apis.authentication.cache.model.UserCache;
import io.github.lets.apis.authentication.detail.fetch.GoogleAuthenticationUserFetchService;
import io.github.lets.apis.authentication.detail.model.UserDetailModel;
import io.github.lets.apis.authentication.google.provider.token.GoogleAuthenticationToken;
import io.github.lets.apis.authentication.google.request.GoogleAuthorizationRequest;
import io.github.lets.apis.authentication.google.response.GoogleCreateTokenResponse;
import io.github.lets.apis.authentication.google.response.GoogleRefreshTokenResponse;
import io.github.lets.apis.authentication.google.response.model.GoogleIdTokenPayload;
import io.github.lets.apis.authentication.google.service.GoogleAuthenticationService;
import io.github.lets.apis.authentication.manager.user.UserCacheManager;
import io.github.lets.apis.authentication.security.model.LetsAuthentication;
import io.github.lets.apis.authentication.util.AuthenticationUtil;
import io.github.lets.apis.authentication.util.GoogleAuthenticationUtil;
import io.github.lets.apis.authentication.manager.token.GoogleTokenManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.util.ObjectUtils;

import java.rmi.AccessException;

/**
 * Manages Google-based authentication operations such as login, authentication,
 * token refresh, and logout.
 *
 * <p>This class interacts with the Google token API, retrieves or initializes user information,
 * and manages tokens using Redis cache.</p>
 */
@RequiredArgsConstructor
public class GoogleAuthenticationManager {

    private final AuthenticationManager authenticationManager;
    private final GoogleAuthenticationService googleAuthenticationService;
    private final GoogleAuthenticationUserFetchService googleAuthenticationUserFetchService;
    private final UserCacheManager userCacheManager;
    private final GoogleTokenManager tokenManager;

    /**
     * Performs Google login using the provided authorization code.
     * Calls Google's token API, parses the ID token, and stores user information in cache.
     *
     * @param request the authorization request containing the code from Google
     * @return a cached token object including access and refresh tokens
     * @throws Exception if authentication fails or token parsing encounters an error
     */
    public TokenCache login(GoogleAuthorizationRequest request) throws Exception {
        GoogleAuthenticationToken authenticationToken = new GoogleAuthenticationToken(request);
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        GoogleCreateTokenResponse tokenResponse = (GoogleCreateTokenResponse) authenticate.getPrincipal();
        GoogleIdTokenPayload payload = GoogleAuthenticationUtil.getPayload(tokenResponse.getIdToken());
        UserDetailModel userDetailModel = googleAuthenticationUserFetchService.initOrFetchUserDetails(payload.getEmail(), payload);
        userCacheManager.create(userDetailModel);
        return tokenManager.create(payload.getEmail(), tokenResponse);
    }

    /**
     * Authenticates a user using an existing access token without calling Google's API.
     * Validates the token from Redis cache and retrieves user session details.
     *
     * @param accessToken the access token to validate
     * @return authenticated session containing principal and roles
     * @throws AccessException if the token is invalid or expired
     */
    public LetsAuthentication authenticate(String accessToken) throws AccessException {
        TokenCache tokenCache = tokenManager.get(accessToken);
        AuthenticationUtil.authenticate(tokenCache);
        UserCache userCache = userCacheManager.get(tokenCache.getUsername());
        Object principal = !ObjectUtils.isEmpty(userCache.getDetail()) ? userCache.getDetail().getPrincipal() : null;
        return new LetsAuthentication(accessToken, principal, userCache.getRoles(), true);
    }

    /**
     * Refreshes the access token by calling Google's token API using the refresh token.
     * Updates the token data in Redis cache.
     *
     * @param currentAccessToken the current (soon to expire or expired) access token
     * @param refreshToken the long-lived refresh token
     * @return a new TokenCache with updated access and refresh token info
     * @throws Exception if the refresh operation fails
     */
    public TokenCache refresh(String currentAccessToken, String refreshToken) throws Exception {
        GoogleRefreshTokenResponse tokenResponse = googleAuthenticationService.refresh(refreshToken);
        GoogleIdTokenPayload payload = GoogleAuthenticationUtil.getPayload(tokenResponse.getIdToken());
        return tokenManager.refresh(payload.getEmail(), currentAccessToken, refreshToken, tokenResponse);
    }

    /**
     * Logs out the user by removing the associated access token from Redis cache.
     *
     * @param accessToken the token to invalidate
     */
    public void logout(String accessToken) {
        tokenManager.logout(accessToken);
    }
}