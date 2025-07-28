package io.github.lets.apis.authentication.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.security.SecureRandom;
import java.util.Base64;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DefaultTokenGenerator {

    private static final SecureRandom INSTANCE = new SecureRandom();
    private static final int TOKEN_BYTE_LENGTH = 30;

    public static String createToken() {
        byte[] randomBytes = new byte[TOKEN_BYTE_LENGTH];
        INSTANCE.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }
}