package com.test.teamlog.repository;

import com.test.teamlog.entity.Project;
import com.test.teamlog.entity.ProjectJoin;
import com.test.teamlog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectJoinRepository extends JpaRepository<ProjectJoin, Long> {
    Optional<ProjectJoin> findByProjectAndUser(Project project, User user);
    List<ProjectJoin> findAllByProjectAndIsAcceptedTrueAndIsInvitedFalse(Project project);
    List<ProjectJoin> findAllByUserAndIsAcceptedFalseAndIsInvitedTrue(User user);
}
