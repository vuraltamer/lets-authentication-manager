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
public class UserCache implements Serializable {
    private String username;
    private UserDetailCache detail;
    private Set<String> roles;

    public String toJson() {
        try {
            return Mapper.getInstance().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("UserCache::toJson::{}", e);
        }
    }

    public static UserCache toCache(String value) {
        try {
            if (ObjectUtils.isEmpty(value)) {
                return null;
            }
            return Mapper.getInstance().readValue(value, UserCache.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("UserCache::toCache::{}", e);
        }
    }
}