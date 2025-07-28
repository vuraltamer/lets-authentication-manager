package io.github.lets.apis.authentication.properties;

import io.github.lets.apis.authentication.enums.TokenType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "lets.apis.authentication")
public class AuthenticationConfigurationProperties {

    private Ttl ttl = new Ttl();
    private Token token = new Token();

    @Getter
    @Setter
    public static class Ttl {
        private Long accessToken = 7200L;
        private Long refreshToken = 86400L;
        private Long userDetail = 864000L;
    }

    @Getter
    @Setter
    public static class Token {
        private DefaultToken init;
        private Google google;
    }

    @Getter
    @Setter
    public static class DefaultToken {
        private TokenType type; // JWT or CACHEABLE
        private String jwtSecret;
    }

    @Getter
    @Setter
    public static class Google {
        private boolean enabled;
        private String clientId;
        private String clientSecret;
        private String redirectUri;
        private String grantType;
    }
}