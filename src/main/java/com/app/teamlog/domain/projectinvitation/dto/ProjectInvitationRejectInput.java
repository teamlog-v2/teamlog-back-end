package com.app.teamlog.domain.projectinvitation.dto;

import lombok.Data;

@Data
public class ProjectInvitationRejectInput {
    private Long projectIdx;
    private Long inviteeIdx;

}
