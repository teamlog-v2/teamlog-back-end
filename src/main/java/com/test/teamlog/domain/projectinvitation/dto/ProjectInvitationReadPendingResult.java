package com.test.teamlog.domain.projectinvitation.dto;

import com.test.teamlog.domain.projectinvitation.entity.ProjectInvitation;
import lombok.Data;

@Data
public class ProjectInvitationReadPendingResult {
    private Long idx;
    private Long projectIdx;
    private String projectName;
    private String thumbnail;

    public static ProjectInvitationReadPendingResult from(ProjectInvitation projectInvitation) {
        final ProjectInvitationReadPendingResult result = new ProjectInvitationReadPendingResult();
        result.setIdx(projectInvitation.getIdx());
        result.setProjectIdx(projectInvitation.getProject().getId());
        result.setProjectName(projectInvitation.getProject().getName());
        result.setThumbnail(projectInvitation.getProject().getThumbnail());

        return result;
    }
}
