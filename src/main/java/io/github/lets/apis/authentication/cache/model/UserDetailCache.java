package io.github.lets.apis.authentication.cache.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.lets.apis.authentication.cache.model.deserializer.UserDetailDeserializer;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonDeserialize(using = UserDetailDeserializer.class)
public class UserDetailCache {
    private String className;
    private Object principal;

    public static UserDetailCache create(Object principal) {
        return new UserDetailCache(principal.getClass().getName(), principal);
    }
}