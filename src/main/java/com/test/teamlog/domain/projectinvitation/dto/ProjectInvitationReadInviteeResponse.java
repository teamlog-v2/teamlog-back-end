package com.test.teamlog.domain.projectinvitation.dto;

import lombok.Data;

@Data
public class ProjectInvitationReadInviteeResponse {
    private String inviteeIdentification;
    private String inviteeName;
    private String inviteeProfileImgPath;

    private String inviterIdentification;
    private String inviterName;

    public static ProjectInvitationReadInviteeResponse from(ProjectInvitationReadInviteeResult result) {
        final ProjectInvitationReadInviteeResponse response = new ProjectInvitationReadInviteeResponse();
        response.setInviteeIdentification(result.getInviteeIdentification());
        response.setInviteeName(result.getInviteeName());
        response.setInviteeProfileImgPath(result.getInviteeProfileImgPath());

        response.setInviterIdentification(result.getInviterIdentification());
        response.setInviterName(result.getInviterName());

        return response;
    }
}
