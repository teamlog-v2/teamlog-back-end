package com.test.teamlog.domain.account.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SignInRequest {
    @NotBlank
    private String identification;
    @NotBlank
    private String password;

    public SignInInput toInput() {
        SignInInput input = new SignInInput();
        input.setIdentification(identification);
        input.setPassword(password);

        return input;
    }
}
