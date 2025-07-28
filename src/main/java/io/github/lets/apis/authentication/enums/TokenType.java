package io.github.lets.apis.authentication.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

@Getter
@Component
@AllArgsConstructor
public enum TokenType {
    JWT,
    CACHEABLE,
    GOOGLE,
    MEMORY
}