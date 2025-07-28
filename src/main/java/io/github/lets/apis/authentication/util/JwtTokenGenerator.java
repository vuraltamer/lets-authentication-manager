package io.github.lets.apis.authentication.util;

import io.jsonwebtoken.Jwts;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtTokenGenerator {

    public static String generateToken(String username, Set<String> roles, Long ttl, SecretKey secret) {
        LocalDateTime expirationDateTime = LocalDateTime.now().plusSeconds(ttl);
        Date expirationDate = Date.from(expirationDateTime.atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .signWith(secret)
                .compact();
    }
}