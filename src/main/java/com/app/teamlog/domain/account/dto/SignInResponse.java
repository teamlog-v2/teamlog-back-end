package com.app.teamlog.domain.account.dto;

import lombok.Data;

@Data
public class SignInResponse {
    private String token;

    public static SignInResponse of(SignInResult result) {
        SignInResponse response = new SignInResponse();
        response.setToken(result.getAccessToken());

        return response;
    }
}
