package com.app.teamlog.domain.account.dto;

import com.app.teamlog.domain.token.dto.CreateTokenResult;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignInResult {
    private String accessToken;
    private String refreshToken;

    public static SignInResult from(CreateTokenResult result) {
        return new SignInResult(result.getAccessToken(), result.getRefreshToken());
    }
}
