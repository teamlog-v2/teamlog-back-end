package com.test.teamlog.domain.accountfollow.dto;

import lombok.Data;

@Data
public class AccountFollowingReadResponse {
    private String identification;
    private String name;
    private String profileImgPath;
    private Boolean isFollow;

    public static AccountFollowingReadResponse from(AccountFollowingReadResult result) {
        AccountFollowingReadResponse response = new AccountFollowingReadResponse();
        response.setIdentification(result.getIdentification());
        response.setName(result.getName());
        response.setProfileImgPath(result.getProfileImgPath());

        return response;

    }
}
