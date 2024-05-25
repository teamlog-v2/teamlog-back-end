package com.app.teamlog.domain.projectfollow.service.query;

import com.app.teamlog.domain.account.model.Account;
import com.app.teamlog.domain.projectfollow.entity.ProjectFollower;
import com.app.teamlog.domain.projectfollow.repository.ProjectFollowerRepository;
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
