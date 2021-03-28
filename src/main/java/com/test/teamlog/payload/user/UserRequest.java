package com.test.teamlog.payload.user;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserRequest {
    @NotBlank(message = "올바른 값 입력해주세요")
    private String id;
    @NotBlank(message = "올바른 값 입력해주세요")
    private String password;
    @NotBlank(message = "올바른 값 입력해주세요")
    private String name;
    private String introduction;
    private String profileImgPath;
}
