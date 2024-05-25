package com.app.teamlog.domain.account.repository;

import com.app.teamlog.domain.account.model.Account;

import java.util.List;

public interface AccountRepositoryCustom {
    boolean isFollow(String accountaccountId, String toAccountId);

    List<Account> findAccountNotInProjectMember(Long projectId);
}
