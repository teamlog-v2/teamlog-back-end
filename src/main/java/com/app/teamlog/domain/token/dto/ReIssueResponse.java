package com.app.teamlog.domain.token.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReIssueResponse {
    private String accessToken;

    public static ReIssueResponse of(ReIssueResult result) {
        ReIssueResponse response = new ReIssueResponse();
        response.setAccessToken(result.getAccessToken());

        return response;
    }
}
