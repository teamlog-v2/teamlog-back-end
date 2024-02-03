package com.test.teamlog.domain.account.dto;

import com.test.teamlog.domain.account.model.AuthType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class SignUpRequest {
    @NotBlank(message = "빈문자열, 공백만 입력할 수 없습니다.")
    private String identification;
    @NotBlank(message = "빈문자열, 공백만 입력할 수 없습니다.")
    private String password;
    @NotBlank(message = "빈문자열, 공백만 입력할 수 없습니다.")
    private String name;
    private AuthType authType = AuthType.TEAMLOG;

    public SignUpInput toInput() {
        SignUpInput signUpInput = new SignUpInput();
        signUpInput.setIdentification(this.identification);
        signUpInput.setPassword(this.password);
        signUpInput.setName(this.name);
        signUpInput.setAuthType(this.authType);

        return signUpInput;
    }
}