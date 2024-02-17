package com.test.teamlog.global.security;

import com.test.teamlog.domain.account.model.Account;
import lombok.Getter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collections;
import java.util.Map;

// TODO: 진, global <-> domain 간 엉켜있는 의존관계를 고민해보자
@Getter
public class AccountAdapter extends User implements OAuth2User {
    private final Account account;
    private final Map<String, Object> attributes;

    public AccountAdapter(Account account) {
        super(account.getIdentification(), account.getPassword(), Collections.emptySet());
        this.account = account;
        this.attributes = Collections.emptyMap();
    }

    public AccountAdapter(Account account, Map<String, Object> attributes) {
        super(account.getIdentification(), account.getPassword(), Collections.emptySet());
        this.account = account;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return account.getIdentification();
    }
}
