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
public class GoogleCreateTokenRequest {
    @JsonProperty("code")
    private String authorizationCode;
    @JsonProperty("redirect_uri")
    private String redirectUri;
    @JsonProperty("client_id")
    private String clientId;
    @JsonProperty("client_secret")
    private String clientSecret;
    private String scope;
    @JsonProperty("grant_type")
    private String grantType;

    public static GoogleCreateTokenRequest create(String authorizationCode, AuthenticationConfigurationProperties.Google googleProperties) {
        return GoogleCreateTokenRequest.builder()
                .authorizationCode(authorizationCode)
                .clientId(googleProperties.getClientId())
                .clientSecret(googleProperties.getClientSecret())
                .redirectUri(googleProperties.getRedirectUri())
                .grantType(googleProperties.getGrantType())
                .build();
    }
}