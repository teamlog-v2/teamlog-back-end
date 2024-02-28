package com.test.teamlog.domain.projectfollow.service.query;

import com.test.teamlog.domain.account.model.Account;
import com.test.teamlog.domain.projectfollow.entity.ProjectFollower;
import com.test.teamlog.domain.projectfollow.repository.ProjectFollowerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectFollowQueryService {
    private final ProjectFollowerRepository projectFollowerRepository;

    public List<ProjectFollower> findAllByAccount(Account account) {
        return projectFollowerRepository.findAllByAccount(account);
    }
}
