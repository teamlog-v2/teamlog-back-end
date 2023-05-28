package com.test.teamlog.domain.account.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignInResult {
    private String accessToken;
    private String refreshToken;
}
