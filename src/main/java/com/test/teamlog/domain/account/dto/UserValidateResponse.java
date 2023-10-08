package com.test.teamlog.domain.account.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserValidateResponse {
    private String id;
    private String name;
    private String profileImgPath;

    public static UserValidateResponse from(UserValidateResult result) {
        UserValidateResponse response = new UserValidateResponse();
        response.setId(result.getId());
        response.setName(result.getName());
        response.setProfileImgPath(result.getProfileImgPath());

        return response;
    }
}
