package com.app.teamlog.domain.account.dto;

import com.app.teamlog.domain.account.model.Account;
import lombok.Data;

@Data
public class SignUpResult {
    private String identification;
    private String name;

    public static SignUpResult from(Account account) {
        SignUpResult result = new SignUpResult();
        result.setIdentification(account.getIdentification());
        result.setName(account.getName());

        return result;
    }
}
