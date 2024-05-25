package com.app.teamlog.domain.projectinvitation.dto;

import lombok.Data;

@Data
public class ProjectInvitationReadPendingResponse {
    private Long idx;
    private Long projectIdx;
    private String projectName;
    private String thumbnail;

    public static ProjectInvitationReadPendingResponse from(ProjectInvitationReadPendingResult result) {
        final ProjectInvitationReadPendingResponse response = new ProjectInvitationReadPendingResponse();
        response.setIdx(result.getIdx());
        response.setProjectIdx(result.getProjectIdx());
        response.setProjectName(result.getProjectName());
        response.setThumbnail(result.getThumbnail());

        return response;
    }
}
