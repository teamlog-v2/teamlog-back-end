package com.test.teamlog.domain.accountfollow.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AccountFollowerReadResponse {
    @JsonProperty("id")
    private String identification;
    private String name;
    private String profileImgPath;
    private Boolean isFollow;

    public static AccountFollowerReadResponse from(AccountFollowerReadResult result) {
        AccountFollowerReadResponse response = new AccountFollowerReadResponse();
        response.setIdentification(result.getIdentification());
        response.setName(result.getName());
        response.setProfileImgPath(result.getProfileImgPath());

        return response;

    }
}
