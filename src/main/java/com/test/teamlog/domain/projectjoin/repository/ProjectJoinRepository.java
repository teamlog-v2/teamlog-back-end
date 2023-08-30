package com.test.teamlog.domain.projectjoin.repository;

import com.test.teamlog.domain.account.model.User;

import com.test.teamlog.domain.project.entity.Project;
import com.test.teamlog.domain.projectjoin.entity.ProjectJoin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectJoinRepository extends JpaRepository<ProjectJoin, Long> {
    List<ProjectJoin> findAllByProject(Project project);
    Optional<ProjectJoin> findByProjectAndUser(Project project, User user);
    List<ProjectJoin> findAllByProjectAndIsAcceptedFalseAndIsInvitedTrue(Project project);
    List<ProjectJoin> findAllByProjectAndIsAcceptedTrueAndIsInvitedFalse(Project project);
    List<ProjectJoin> findAllByUserAndIsAcceptedFalseAndIsInvitedTrue(User user);
    List<ProjectJoin> findAllByUserAndIsAcceptedTrueAndIsInvitedFalse(User user);
}
