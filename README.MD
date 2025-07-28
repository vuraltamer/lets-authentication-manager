# 🛡️ Lets Authentication Manager

A modular authentication starter for Spring Boot applications, supporting `Redis`, `JWT`, and `Google OAuth 2.0`. Each component is conditionally enabled using `@ConditionalOnProperty` and `@ConditionalOnClass`.

---

## 🚀 Features

- ✅ Redis-based access/refresh token management
- ✅ Stateless JWT token management
- ✅ Google OAuth 2.0 authentication support
- ✅ Token strategy resolver for dynamic token handling
- ✅ User and token caching services (via Redis)
- ✅ `UserDetailManagerAspect` via Spring AOP
- ✅ Pluggable `AuthenticationManager` with both standard and Google auth support
- 🔌 Seamless Spring Boot AutoConfiguration support

---

## ⚙️ Installation

**yml configuration**


    lets:
        apis:
            authentication:
                token:
                    init:
                        type: CACHEABLE
                        jwt-secret:
                    google:
                        enabled: true
                        client-id:
                        client-secret:
                        redirect-uri:
                        grant-type: authorization_code
                ttl:
                    access-token: 7200    # 2h
                    refresh-token: 86400  # 24h
                    user-detail: 864000   # 10d


**Bean Definition**


    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http,
        AuthenticationDetailServiceImpl userDetailService,
        GoogleAuthenticationProvider googleAuthenticationProvider) throws Exception {

        return http.getSharedObject(AuthenticationManagerBuilder.class)
            .authenticationProvider(daoAuthenticationProvider(userDetailService))
            .authenticationProvider(googleAuthenticationProvider)
            .build();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(AuthenticationDetailServiceImpl userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

### Maven

```xml
<dependency>
  <groupId>io.github.lets.apis</groupId>
  <artifactId>lets-authentication-manager</artifactId>
  <version>1.0.0</version>
</dependency>
