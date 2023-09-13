package com.test.teamlog.domain.projectjoin.service.query;

import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.domain.project.entity.Project;
import com.test.teamlog.domain.projectjoin.entity.ProjectJoin;
import com.test.teamlog.domain.projectjoin.repository.ProjectJoinRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectJoinQueryService {
    private final ProjectJoinRepository projectJoinRepository;

    public List<ProjectJoin> findAllByProject(Project project) {
        return projectJoinRepository.findAllByProject(project);
    }

    public Optional<ProjectJoin> findByProjectAndUser(Project project, User user) {
        return projectJoinRepository.findByProjectAndUser(project, user);
    }

    public List<ProjectJoin> findAllByProjectAndIsAcceptedFalseAndIsInvitedTrue(Project project) {
        return projectJoinRepository.findAllByProjectAndIsAcceptedFalseAndIsInvitedTrue(project);
    }

    public List<ProjectJoin> findAllByProjectAndIsAcceptedTrueAndIsInvitedFalse(Project project) {
        return projectJoinRepository.findAllByProjectAndIsAcceptedTrueAndIsInvitedFalse(project);
    }

    public List<ProjectJoin> findAllByUserAndIsAcceptedFalseAndIsInvitedTrue(User user) {
        return projectJoinRepository.findAllByUserAndIsAcceptedFalseAndIsInvitedTrue(user);
    }

    public List<ProjectJoin> findAllByUserAndIsAcceptedTrueAndIsInvitedFalse(User user) {
        return projectJoinRepository.findAllByUserAndIsAcceptedTrueAndIsInvitedFalse(user);
    }
}
