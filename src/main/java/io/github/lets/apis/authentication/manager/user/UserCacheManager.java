package io.github.lets.apis.authentication.manager.user;

import io.github.lets.apis.authentication.cache.UserCacheService;
import io.github.lets.apis.authentication.cache.model.UserCache;
import io.github.lets.apis.authentication.cache.model.UserDetailCache;
import io.github.lets.apis.authentication.detail.model.UserDetailModel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class UserCacheManager {

    private final UserCacheService userCacheService;

    public UserCache create(String username, Collection<? extends GrantedAuthority> authorities) {
        UserCache userCache = createUser(username, authorities);
        userCacheService.create(userCache);
        return userCache;
    }

    public UserCache get(String username) {
        return userCacheService.get(username);
    }

    private static UserCache createUser(String username, Collection<? extends GrantedAuthority> grantedAuthorities) {
        return UserCache.builder()
                .username(username)
                .roles(createAuthority(grantedAuthorities))
                .build();
    }

    private static Set<String> createAuthority(Collection<? extends GrantedAuthority> grantedAuthority) {
        return grantedAuthority.stream()
                .map(auth -> auth.getAuthority())
                .collect(Collectors.toSet());
    }

    public UserCache create(UserDetailModel userDetailModel) {
        UserCache googleUser = UserCache.builder()
                .username(userDetailModel.getUsername())
                .detail(new UserDetailCache(userDetailModel.getDetails().getClass().getName(), userDetailModel.getDetails()))
                .roles(Set.of("GOOGLE_USER"))
                .build();
        userCacheService.create(googleUser);
        return googleUser;
    }
}