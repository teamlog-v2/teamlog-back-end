package com.app.teamlog.domain.projectapplication.repository;

import com.app.teamlog.domain.account.model.Account;
import com.app.teamlog.domain.project.entity.Project;
import com.app.teamlog.domain.projectapplication.entity.ProjectApplication;
import com.app.teamlog.domain.projectinvitation.repository.ProjectInvitationCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectApplicationRepository extends JpaRepository<ProjectApplication, Long>, ProjectInvitationCustom {
    Optional<ProjectApplication> findByProjectAndApplicant(Project project, Account applicant);

    List<ProjectApplication> findAllByProject(Project project);

    List<ProjectApplication> findAllByApplicant(Account applicant);
}