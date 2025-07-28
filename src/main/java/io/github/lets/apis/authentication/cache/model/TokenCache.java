package io.github.lets.apis.authentication.cache.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.lets.apis.authentication.mapper.Mapper;
import lombok.*;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenCache implements Serializable {
    private String accessToken;
    private String refreshToken;
    private int accessTokenExpireTime;
    private String username;
    private Set<String> roles;

    public String toJson() {
        try {
            return Mapper.getInstance().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("TokenCache::toJson::error::{}", e);
        }
    }

    public static TokenCache toCache(String value) {
        try {
            if (ObjectUtils.isEmpty(value)) {
                return null;
            }
            return Mapper.getInstance().readValue(value, TokenCache.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("TokenCache::toCache:::error::{}", e);
        }
    }
}