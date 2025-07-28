package io.github.lets.apis.authentication.google.service;

import io.github.lets.apis.authentication.google.request.GoogleCreateTokenRequest;
import io.github.lets.apis.authentication.google.request.GoogleRefreshTokenRequest;
import io.github.lets.apis.authentication.google.response.GoogleCreateTokenResponse;
import io.github.lets.apis.authentication.google.response.GoogleRefreshTokenResponse;
import io.github.lets.apis.authentication.properties.AuthenticationConfigurationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
public class GoogleAuthenticationService {

    private final AuthenticationConfigurationProperties properties;
    private final @Qualifier("letsAuthenticationRestTemplate") RestTemplate restTemplate;

    private static final String TOKEN_URL = "https://oauth2.googleapis.com/token";

    public GoogleCreateTokenResponse authenticate(String authorizationCode) {
        GoogleCreateTokenRequest createTokenRequest = GoogleCreateTokenRequest.create(authorizationCode, properties.getToken().getGoogle());
        return restTemplate.postForObject(TOKEN_URL, createTokenRequest, GoogleCreateTokenResponse.class);
    }

    public GoogleRefreshTokenResponse refresh(String refreshToken) {
        GoogleRefreshTokenRequest createTokenRequest = GoogleRefreshTokenRequest.create(refreshToken, properties.getToken().getGoogle());
        return restTemplate.postForObject(TOKEN_URL, createTokenRequest, GoogleRefreshTokenResponse.class);
    }
}