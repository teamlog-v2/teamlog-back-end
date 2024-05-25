package com.app.teamlog.domain.account.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SignUpResponse {
    private String identification;
    private String name;

    public static SignUpResponse from(SignUpResult result) {
        SignUpResponse response = new SignUpResponse();
        response.setIdentification(result.getIdentification());
        response.setName(result.getName());

        return response;
    }
}