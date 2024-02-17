package com.test.teamlog.domain.account.dto;

import com.test.teamlog.domain.account.model.AuthType;
import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.global.utility.PasswordUtil;
import lombok.Data;

@Data
public class SignUpInput {
    private String identification;
    private String password;
    private String name;
    private AuthType authType;

    public User toUser() {
        return User.builder()
                .identification(identification)
                .password(PasswordUtil.encode(password))
                .name(name)
                .authType(authType)
                .build();
    }
}
