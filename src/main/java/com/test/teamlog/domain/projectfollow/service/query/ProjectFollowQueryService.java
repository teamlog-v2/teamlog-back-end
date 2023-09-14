package com.test.teamlog.domain.projectfollow.service.query;

import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.domain.projectfollow.entity.ProjectFollower;
import com.test.teamlog.domain.projectfollow.repository.ProjectFollowerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectFollowQueryService {
    private final ProjectFollowerRepository projectFollowerRepository;

    public List<ProjectFollower> findAllByUser(User user) {
        return projectFollowerRepository.findAllByUser(user);
    }
}
