package io.github.lets.apis.authentication.security;

import io.github.lets.apis.authentication.security.model.AuthenticationDetail;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * A service interface used to load user details by username.
 *
 * <p>This is typically used during authentication to retrieve user information
 * based on the provided username.</p>
 *
 * <p>Returns an {@link AuthenticationDetail} object containing user data.</p>
 */
public interface AuthenticationDetailService extends UserDetailsService {

    @Override
    AuthenticationDetail loadUserByUsername(String username);
}