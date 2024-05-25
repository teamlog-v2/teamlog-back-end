package com.app.teamlog.domain.projectinvitation.dto;

import com.app.teamlog.domain.account.model.Account;
import com.app.teamlog.domain.project.entity.Project;
import com.app.teamlog.domain.projectinvitation.entity.ProjectInvitation;
import lombok.Data;

@Data
public class ProjectInvitationCreateInput {
    private Long projectIdx;
    private Long inviterIdx;
    private Long inviteeIdx;

    public ProjectInvitation toProjectInvitation(Project project, Account inviter, Account invitee) {
        return ProjectInvitation.builder()
                .project(project)
                .inviter(inviter)
                .invitee(invitee)
                .build();
    }
}
