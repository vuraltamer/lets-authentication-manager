package io.github.lets.apis.authentication.detail.loader;

import io.github.lets.apis.authentication.cache.UserCacheService;
import io.github.lets.apis.authentication.cache.model.UserCache;
import io.github.lets.apis.authentication.cache.model.UserDetailCache;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AuthenticationUserLoadService {

    private final UserCacheService userCacheService;

    public void load(String username, Object userDetails) {
        UserCache userCache = userCacheService.get(username);
        userCache.setDetail(UserDetailCache.create(userDetails));
        userCacheService.create(userCache);
    }
}