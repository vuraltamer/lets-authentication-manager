package io.github.lets.apis.authentication.detail.manager.aop;

import io.github.lets.apis.authentication.detail.manager.UserDetailManager;
import io.github.lets.apis.authentication.cache.model.TokenCache;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
@RequiredArgsConstructor
public class UserDetailManagerAspect {

    private final UserDetailManager userDetailManager;

    @Around("@annotation(io.github.lets.apis.authentication.detail.manager.aop.LoadUserDetail)")
    public Object aroundLoadUserMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        if (result instanceof TokenCache) {
            userDetailManager.loadUserDetails(((TokenCache) result).getUsername());
        }
        return result;
    }
}