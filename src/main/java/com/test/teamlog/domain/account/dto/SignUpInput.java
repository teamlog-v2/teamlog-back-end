package com.test.teamlog.domain.account.dto;

import com.test.teamlog.domain.account.model.Account;
import com.test.teamlog.domain.account.model.AuthType;
import com.test.teamlog.global.utility.PasswordUtil;
import lombok.Data;

@Data
public class SignUpInput {
    private String identification;
    private String password;
    private String name;
    private AuthType authType;

    public Account toAccount() {
        return Account.builder()
                .identification(identification)
                .password(PasswordUtil.encode(password))
                .name(name)
                .authType(authType)
                .build();
    }
}
