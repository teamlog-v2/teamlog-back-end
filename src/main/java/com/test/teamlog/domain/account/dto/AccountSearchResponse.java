package com.test.teamlog.domain.account.dto;

import lombok.Data;

@Data
public class AccountSearchResponse {
    private String id;
    private String name;
    private String profileImgPath;

    public static AccountSearchResponse from(AccountSearchResult result) {
        AccountSearchResponse response = new AccountSearchResponse();
        response.setId(result.getId());
        response.setName(result.getName());
        response.setProfileImgPath(result.getProfileImgPath());

        return response;
    }
}
