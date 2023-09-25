package com.test.teamlog.domain.projectinvitation.repository;

import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.domain.project.entity.Project;
import com.test.teamlog.domain.projectinvitation.dto.ProjectInvitationReadInviteeResult;
import com.test.teamlog.domain.projectinvitation.dto.ProjectInvitationReadPendingResult;

import java.util.List;

public interface ProjectInvitationCustom {
    List<ProjectInvitationReadInviteeResult> findAllByProjectAndAcceptedIsFalse(Project project);
    List<ProjectInvitationReadPendingResult> findAllByUserAndAcceptedIsFalse(User user);
}
