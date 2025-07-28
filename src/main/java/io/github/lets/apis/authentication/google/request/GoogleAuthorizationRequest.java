package io.github.lets.apis.authentication.google.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GoogleAuthorizationRequest {
    private String code;
    private String scope;
    private Integer authuser;
    private String hd;
    private String prompt;
}