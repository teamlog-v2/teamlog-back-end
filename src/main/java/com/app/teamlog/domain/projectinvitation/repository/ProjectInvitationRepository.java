package com.app.teamlog.domain.projectinvitation.repository;

import com.app.teamlog.domain.account.model.Account;
import com.app.teamlog.domain.project.entity.Project;
import com.app.teamlog.domain.projectinvitation.entity.ProjectInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ProjectInvitationRepository extends JpaRepository<ProjectInvitation, Long>, ProjectInvitationCustom {
    List<ProjectInvitation> findAllByInvitee(Account invitee);

    List<ProjectInvitation> findAllByProjectAndInviter(Project project, Account inviter);

    Optional<ProjectInvitation> findByProjectAndInvitee(Project project, Account invitee);
}
