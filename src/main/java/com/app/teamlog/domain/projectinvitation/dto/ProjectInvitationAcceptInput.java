package com.app.teamlog.domain.projectinvitation.dto;

import lombok.Data;

@Data
public class ProjectInvitationAcceptInput {
    private Long projectIdx;
    private Long inviteeIdx;
}
