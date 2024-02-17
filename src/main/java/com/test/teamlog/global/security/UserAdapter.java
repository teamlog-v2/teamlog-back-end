package com.test.teamlog.global.security;

import com.test.teamlog.domain.account.model.User;
import lombok.Getter;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collections;
import java.util.Map;

// TODO: 진, global <-> domain 간 엉켜있는 의존관계를 고민해보자
@Getter
public class UserAdapter extends org.springframework.security.core.userdetails.User implements OAuth2User {
    private final User user;
    private final Map<String, Object> attributes;

    public UserAdapter(User user) {
        super(user.getIdentification(), user.getPassword(), Collections.emptySet());
        this.user = user;
        this.attributes = Collections.emptyMap();
    }

    public UserAdapter(User user, Map<String, Object> attributes) {
        super(user.getIdentification(), user.getPassword(), Collections.emptySet());
        this.user = user;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return user.getIdentification();
    }
}
