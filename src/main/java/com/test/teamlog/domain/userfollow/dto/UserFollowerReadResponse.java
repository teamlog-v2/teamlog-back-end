package com.test.teamlog.domain.userfollow.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserFollowerReadResponse {
    @JsonProperty("id")
    private String identification;
    private String name;
    private String profileImgPath;
    private Boolean isFollow;

    public static UserFollowerReadResponse from(UserFollowerReadResult result) {
        UserFollowerReadResponse response = new UserFollowerReadResponse();
        response.setIdentification(result.getIdentification());
        response.setName(result.getName());
        response.setProfileImgPath(result.getProfileImgPath());

        return response;

    }
}
