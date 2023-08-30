package com.test.teamlog.domain.projectfollow.repository;

import com.test.teamlog.domain.account.model.User;

import com.test.teamlog.domain.project.entity.Project;
import com.test.teamlog.domain.projectfollow.entity.ProjectFollower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectFollowerRepository extends JpaRepository<ProjectFollower, Long> {
    List<ProjectFollower> findAllByUser(User user);
    List<ProjectFollower> findAllByProject(Project project);
    Optional<ProjectFollower> findByProjectAndUser(Project project,User user);

}
