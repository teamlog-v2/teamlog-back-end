package com.test.teamlog.domain.projectinvitation.dto;

import lombok.Data;

@Data
public class ProjectInvitationDeleteInput {
    private Long projectIdx;
    private Long inviterIdx;
    private Long inviteeIdx;
}
