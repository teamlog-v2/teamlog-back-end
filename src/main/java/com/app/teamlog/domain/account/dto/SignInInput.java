package com.app.teamlog.domain.account.dto;

import lombok.Data;

@Data
public class SignInInput {
    private String identification;
    private String password;
}
