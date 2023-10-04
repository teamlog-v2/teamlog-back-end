package com.test.teamlog.domain.projectinvitation.dto;

import lombok.Data;

@Data
public class ProjectInvitationRejectRequest {
    private Long projectIdx;

    public ProjectInvitationRejectInput toInput(Long inviteeIdx) {
        ProjectInvitationRejectInput input = new ProjectInvitationRejectInput();
        input.setProjectIdx(this.projectIdx);
        input.setInviteeIdx(inviteeIdx);

        return input;
    }
}
