package com.app.teamlog.domain.account.service.command;

import com.app.teamlog.domain.account.model.Account;
import com.app.teamlog.domain.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountCommandService {
    private final AccountRepository accountRepository;

    public Account save(Account account) {
        return accountRepository.save(account);
    }
}
