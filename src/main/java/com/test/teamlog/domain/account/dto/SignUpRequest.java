package com.test.teamlog.domain.account.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SignUpRequest {
    @NotBlank(message = "빈문자열, 공백만 입력할 수 없습니다.")
    private String identification;
    @NotBlank(message = "빈문자열, 공백만 입력할 수 없습니다.")
    private String password;
    @NotBlank(message = "빈문자열, 공백만 입력할 수 없습니다.")
    private String name;

    public SignUpInput toInput() {
        SignUpInput signUpInput = new SignUpInput();
        signUpInput.setIdentification(this.identification);
        signUpInput.setPassword(this.password);
        signUpInput.setName(this.name);

        return signUpInput;
    }
}