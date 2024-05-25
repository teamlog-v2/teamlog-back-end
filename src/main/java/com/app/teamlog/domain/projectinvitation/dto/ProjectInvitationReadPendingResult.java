package com.app.teamlog.domain.projectinvitation.dto;

import com.app.teamlog.domain.project.entity.Project;
import com.app.teamlog.domain.projectinvitation.entity.ProjectInvitation;
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

        final Project project = projectInvitation.getProject();
        result.setProjectIdx(project.getId());
        result.setProjectName(project.getName());

        if (project.getThumbnail() != null) result.setThumbnail(project.getThumbnail().getStoredFilePath());

        return result;
    }
}
