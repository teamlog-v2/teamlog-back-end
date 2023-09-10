package com.test.teamlog.domain.account.dto;

import lombok.Data;

@Data
public class UserReadDetailResponse {
    private Boolean isMe;
    private Boolean isFollow;
    private String identification;
    private String name;
    private String introduction;
    private String profileImgPath;

    public static UserReadDetailResponse from(UserReadDetailResult result) {
        UserReadDetailResponse response = new UserReadDetailResponse();
        response.setIsMe(result.getIsMe());
        response.setIsFollow(result.getIsFollow());
        response.setIdentification(result.getIdentification());
        response.setName(result.getName());
        response.setIntroduction(result.getIntroduction());
        response.setProfileImgPath(result.getProfileImgPath());

        return response;
    }
}
