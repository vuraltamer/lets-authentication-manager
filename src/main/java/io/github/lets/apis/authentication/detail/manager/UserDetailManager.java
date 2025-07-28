package io.github.lets.apis.authentication.detail.manager;

import io.github.lets.apis.authentication.detail.fetch.AuthenticationUserFetchService;
import io.github.lets.apis.authentication.detail.loader.AuthenticationUserLoadService;
import io.github.lets.apis.authentication.detail.model.UserDetailModel;
import org.springframework.scheduling.annotation.Async;

public class UserDetailManager {

    private final AuthenticationUserFetchService userDetailFetchService;
    private final AuthenticationUserLoadService userDetailLoadService;

    public UserDetailManager(AuthenticationUserLoadService userDetailLoadService, AuthenticationUserFetchService userDetailFetchService) {
        this.userDetailLoadService = userDetailLoadService;
        this.userDetailFetchService = userDetailFetchService;
    }

    @Async
    public void loadUserDetails(String username) {
        UserDetailModel model = userDetailFetchService.fetchUserDetails(username);
        userDetailLoadService.load(model.getUsername(), model.getDetails());
    }
}