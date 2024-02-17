package com.test.teamlog.domain.accountfollow.repository;

import com.test.teamlog.domain.account.model.Account;

import com.test.teamlog.domain.accountfollow.entity.AccountFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountFollowRepository extends JpaRepository<AccountFollow, Long> {
    Optional<AccountFollow> findByFromAccountAndToAccount(Account account, Account targetAccount);
    List<AccountFollow> findAllByFromAccount(Account account);
    List<AccountFollow> findAllByToAccount(Account account);
}
