package com.test.teamlog.global.security;

import com.test.teamlog.domain.account.model.User;
import lombok.Getter;

import java.util.Collections;

// TODO: 진, global <-> domain 간 엉켜있는 의존관계를 고민해보자
@Getter
public class UserAdapter extends org.springframework.security.core.userdetails.User {
    private final User user;

    public UserAdapter(User user) {
        super(user.getIdentification(), user.getPassword(), Collections.emptySet());
        user.getProfileImage();
        this.user = user;
    }
}
