package com.test.teamlog.domain.account.dto;

import com.test.teamlog.domain.account.model.User;
import lombok.Data;

@Data
public class SignUpResult {
    private String identification;
    private String name;

    public static SignUpResult from(User user) {
        SignUpResult result = new SignUpResult();
        result.setIdentification(user.getIdentification());
        result.setName(user.getName());

        return result;
    }
}
