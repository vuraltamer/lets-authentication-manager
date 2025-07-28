package io.github.lets.apis.authentication.cache;

import io.github.lets.apis.authentication.cache.base.BaseCacheService;
import io.github.lets.apis.authentication.cache.model.UserCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;

public class UserCacheService extends BaseCacheService {

    private static final String BASE_KEY = "auth:user-detail:";

    @Value("${lets.apis.authentication.ttl.user-detail}")
    private Long userDetailTtl;

    public UserCacheService(RedisTemplate<String, Object> redisTemplate) {
        super(redisTemplate);
    }

    public void create(UserCache userCache) {
        super.create(BASE_KEY + userCache.getUsername() , userCache.toJson(), Duration.ofSeconds(userDetailTtl));
    }

    public UserCache get(String username) {
        final String jsonValue = (String) super.get(BASE_KEY + username);
        return UserCache.toCache(jsonValue);
    }
}