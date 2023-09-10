package com.test.teamlog.domain.account.repository;

public interface AccountRepositoryCustom {
    boolean isFollow(String fromUserId, String toUserId);
}
