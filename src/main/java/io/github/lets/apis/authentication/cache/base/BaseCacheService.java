package io.github.lets.apis.authentication.cache.base;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;


@RequiredArgsConstructor
public class BaseCacheService {

    protected final RedisTemplate<String, Object> redisTemplate;

    public void create(final String key, final Object value, final Duration ttl) {
        redisTemplate.opsForValue().set(key, value, ttl);
    }

    public void delete(final String key) {
        redisTemplate.delete(key);
    }

    public Object get(final String key) {
        return redisTemplate.opsForValue().get(key);
    }
}