package com.test.teamlog.domain.account.service.command;

import com.test.teamlog.domain.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountCommandService {
    private final AccountRepository accountRepository;

}
