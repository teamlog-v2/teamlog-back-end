package com.app.teamlog.domain.account.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccountValidateResponse {
    private String id;
    private String name;
    private String profileImgPath;

    public static AccountValidateResponse from(AccountValidateResult result) {
        AccountValidateResponse response = new AccountValidateResponse();
        response.setId(result.getId());
        response.setName(result.getName());
        response.setProfileImgPath(result.getProfileImgPath());

        return response;
    }
}
