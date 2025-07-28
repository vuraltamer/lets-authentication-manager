package io.github.lets.apis.authentication.detail.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserDetailModel {
    private String username;
    private Object details;
}