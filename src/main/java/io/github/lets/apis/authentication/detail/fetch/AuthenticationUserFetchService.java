package io.github.lets.apis.authentication.detail.fetch;

import io.github.lets.apis.authentication.detail.model.UserDetailModel;

/**
 * Service interface for fetching user details by username.
 *
 * <p>Used during login to retrieve user information, which will then be stored
 * in the Redis session.</p>
 *
 * <p>In subsequent requests, user details are retrieved from Redis instead of calling this service again.</p>
 */
public interface AuthenticationUserFetchService {

    UserDetailModel fetchUserDetails(String username);
}