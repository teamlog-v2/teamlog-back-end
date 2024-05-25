package com.app.teamlog.domain.projectmember.repository;

import com.app.teamlog.domain.account.model.Account;
import com.app.teamlog.domain.project.entity.Project;
import com.app.teamlog.domain.projectmember.entity.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    List<ProjectMember> findByProject(Project project);
    Optional<ProjectMember> findByProjectAndAccount(Project project, Account account);
}
