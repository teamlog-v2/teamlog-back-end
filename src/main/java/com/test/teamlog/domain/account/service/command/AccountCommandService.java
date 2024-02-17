package com.test.teamlog.domain.account.service.command;

import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.domain.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountCommandService {
    private final AccountRepository accountRepository;

    public User save(User account) {
        return accountRepository.save(account);
    }
}
