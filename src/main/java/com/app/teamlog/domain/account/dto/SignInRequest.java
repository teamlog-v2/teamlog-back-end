package com.app.teamlog.domain.account.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


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
