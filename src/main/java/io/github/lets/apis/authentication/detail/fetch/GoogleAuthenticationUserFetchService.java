package io.github.lets.apis.authentication.detail.fetch;

import io.github.lets.apis.authentication.detail.model.UserDetailModel;
import io.github.lets.apis.authentication.google.response.model.GoogleIdTokenPayload;

/**
 * Service interface for initializing or retrieving user details using Google identity data.
 *
 * <p>If the user does not exist in the database, it creates a new user entry.
 * Otherwise, it fetches the existing user.</p>
 *
 * <p>The resulting user detail is then stored in the Redis session for later use.</p>
 */
public interface GoogleAuthenticationUserFetchService {

    UserDetailModel initOrFetchUserDetails(String username, GoogleIdTokenPayload userDetail);
}