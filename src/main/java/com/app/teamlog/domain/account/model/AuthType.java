package com.app.teamlog.domain.account.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum AuthType {
    TEAMLOG("teamlog"),
    GITHUB("github");

    private String name;

    public static AuthType fromName(String name) {
        for (AuthType authType : values()) {
            if (authType.name.equals(name)) {
                return authType;
            }
        }

        throw new IllegalArgumentException("존재하지 않는 authType 이름입니다.: " + name);
    }
}
