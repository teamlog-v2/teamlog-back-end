package com.test.teamlog.domain.account.repository;

import com.test.teamlog.domain.account.model.User;

import java.util.List;

public interface AccountRepositoryCustom {
    boolean isFollow(String fromUserId, String toUserId);

    List<User> findUsersNotInProjectMember(Long projectId);
}
