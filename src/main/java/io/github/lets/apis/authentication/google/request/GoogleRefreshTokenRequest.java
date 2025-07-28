package io.github.lets.apis.authentication.google.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.github.lets.apis.authentication.properties.AuthenticationConfigurationProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoogleRefreshTokenRequest {
    @JsonProperty("client_id")
    private String clientId;
    @JsonProperty("client_secret")
    private String clientSecret;
    @JsonProperty("grant_type")
    private String grantType;
    @JsonProperty("refresh_token")
    private String refreshToken;

    public static GoogleRefreshTokenRequest create(String refreshToken, AuthenticationConfigurationProperties.Google googleProperties) {
        return GoogleRefreshTokenRequest.builder()
                .refreshToken(refreshToken)
                .clientId(googleProperties.getClientId())
                .clientSecret(googleProperties.getClientSecret())
                .grantType(googleProperties.getGrantType())
                .build();
    }
}