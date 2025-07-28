package io.github.lets.apis.authentication.util;

import io.github.lets.apis.authentication.google.response.model.GoogleIdTokenPayload;
import io.github.lets.apis.authentication.mapper.Mapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Base64;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GoogleAuthenticationUtil {

    public static GoogleIdTokenPayload getPayload(String idToken) throws Exception {
        String[] parts = idToken.split("\\.");
        if (parts.length < 2) {
            throw new IllegalArgumentException("Id Token parse error!");
        }
        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
        return Mapper.getInstance().readValue(payloadJson, GoogleIdTokenPayload.class);
    }
}