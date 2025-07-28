package io.github.lets.apis.authentication.security.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Authority {
    private Set<String> authorities;

    public Set<SimpleGrantedAuthority> getSimpleGrantedAuthorities() {
        return getSimpleGrantedAuthorities(this.authorities);
    }

    public static Set<SimpleGrantedAuthority> getSimpleGrantedAuthorities(Set<String> authorities) {
        return authorities.stream()
                .map(e -> new SimpleGrantedAuthority(e))
                .collect(Collectors.toSet());
    }
}