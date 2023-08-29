package com.test.teamlog.domain.userfollow.dto;

import lombok.Data;

@Data
public class UserFollowingReadResponse {
    private String identification;
    private String name;
    private String profileImgPath;
    private Boolean isFollow;

    public static UserFollowingReadResponse from(UserFollowingReadResult result) {
        UserFollowingReadResponse response = new UserFollowingReadResponse();
        response.setIdentification(result.getIdentification());
        response.setName(result.getName());
        response.setProfileImgPath(result.getProfileImgPath());

        return response;

    }
}
