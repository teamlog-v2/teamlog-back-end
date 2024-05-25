package com.app.teamlog.domain.accountfollow.service.query;

import com.app.teamlog.domain.account.model.Account;
import com.app.teamlog.domain.accountfollow.entity.AccountFollow;
import com.app.teamlog.domain.accountfollow.repository.AccountFollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountFollowQueryService {
    private final AccountFollowRepository accountFollowRepository;

    public List<AccountFollow> readAllByFromAccount(Account currentAccount) {
        return accountFollowRepository.findAllByFromAccount(currentAccount);
    }

    public boolean isFollow(Account currentAccount, Account targetAccount) {
        return accountFollowRepository.findByFromAccountAndToAccount(currentAccount, targetAccount).isPresent();
    }
}
