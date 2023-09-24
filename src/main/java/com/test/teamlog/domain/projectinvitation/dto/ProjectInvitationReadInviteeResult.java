package com.test.teamlog.domain.projectinvitation.dto;

import lombok.Data;

@Data
public class ProjectInvitationReadInviteeResult {
    private String inviteeIdentification;
    private String inviteeName;
    private String inviteeProfileImgPath;

    private String inviterIdentification;
    private String inviterName;
}
