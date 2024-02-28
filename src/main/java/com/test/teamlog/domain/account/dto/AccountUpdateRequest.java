package com.test.teamlog.domain.account.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AccountUpdateRequest {
    private String id;
    private String password;
    @NotBlank(message = "빈문자열, 공백만 입력할 수 없습니다.")
    private String name;
    private String introduction;
    private Boolean defaultImage;
}
