package com.test.teamlog.domain.projectinvitation.dto;

import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.domain.project.entity.Project;
import com.test.teamlog.domain.projectinvitation.entity.ProjectInvitation;
import lombok.Data;

@Data
public class ProjectInvitationCreateInput {
    private Long projectIdx;
    private Long inviterIdx;
    private Long inviteeIdx;

    public ProjectInvitation toProjectInvitation(Project project, User inviter, User invitee) {
        return ProjectInvitation.builder()
                .project(project)
                .inviter(inviter)
                .invitee(invitee)
                .build();
    }
}
