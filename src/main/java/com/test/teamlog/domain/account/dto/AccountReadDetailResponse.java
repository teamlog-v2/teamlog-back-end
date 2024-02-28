package com.test.teamlog.domain.account.dto;

import lombok.Data;

@Data
public class AccountReadDetailResponse {
    private Boolean isMe;
    private Boolean isFollow;
    private String identification;
    private String name;
    private String introduction;
    private String profileImgPath;

    public static AccountReadDetailResponse from(AccountReadDetailResult result) {
        AccountReadDetailResponse response = new AccountReadDetailResponse();
        response.setIsMe(result.getIsMe());
        response.setIsFollow(result.getIsFollow());
        response.setIdentification(result.getIdentification());
        response.setName(result.getName());
        response.setIntroduction(result.getIntroduction());
        response.setProfileImgPath(result.getProfileImgPath());

        return response;
    }
}
