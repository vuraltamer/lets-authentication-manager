package io.github.lets.apis.authentication.google.provider;

import io.github.lets.apis.authentication.google.provider.token.GoogleAuthenticationToken;
import io.github.lets.apis.authentication.google.request.GoogleAuthorizationRequest;
import io.github.lets.apis.authentication.google.response.GoogleCreateTokenResponse;
import io.github.lets.apis.authentication.google.service.GoogleAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

@RequiredArgsConstructor
public class GoogleAuthenticationProvider implements AuthenticationProvider {

    private final GoogleAuthenticationService authenticationService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        GoogleAuthorizationRequest authorizationRequest = (GoogleAuthorizationRequest) authentication.getPrincipal();
        GoogleCreateTokenResponse authenticatedToken = authenticationService.authenticate(authorizationRequest.getCode());
        return new GoogleAuthenticationToken(authenticatedToken);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(GoogleAuthenticationToken.class);
    }
}