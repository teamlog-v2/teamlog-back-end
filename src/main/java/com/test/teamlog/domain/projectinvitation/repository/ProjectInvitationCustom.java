package com.test.teamlog.domain.projectinvitation.repository;

import com.test.teamlog.domain.project.entity.Project;
import com.test.teamlog.domain.projectinvitation.dto.ProjectInvitationReadInviteeResult;

import java.util.List;

public interface ProjectInvitationCustom {
    List<ProjectInvitationReadInviteeResult> findAllByProjectAndAcceptedIsFalse(Project project);
}
