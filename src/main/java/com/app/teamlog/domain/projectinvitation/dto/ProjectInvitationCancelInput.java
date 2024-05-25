package com.app.teamlog.domain.projectinvitation.dto;

import lombok.Data;

@Data
public class ProjectInvitationCancelInput {
    private Long projectIdx;
    private Long inviterIdx;
    private Long inviteeIdx;
}
