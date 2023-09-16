package com.test.teamlog.domain.account.dto;

import lombok.Data;

@Data
public class UserSearchResponse {
    private String id;
    private String name;
    private String profileImgPath;

    public static UserSearchResponse from(UserSearchResult result) {
        UserSearchResponse response = new UserSearchResponse();
        response.setId(result.getId());
        response.setName(result.getName());
        response.setProfileImgPath(result.getProfileImgPath());

        return response;
    }
}
