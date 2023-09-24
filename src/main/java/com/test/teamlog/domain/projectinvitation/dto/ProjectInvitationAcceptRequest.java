package com.test.teamlog.domain.projectinvitation.dto;

import lombok.Data;

@Data
public class ProjectInvitationAcceptRequest {
    private Long projectIdx;

    public ProjectInvitationAcceptInput toInput(Long inviteeIdx) {
        ProjectInvitationAcceptInput input = new ProjectInvitationAcceptInput();
        input.setProjectIdx(this.projectIdx);
        input.setInviteeIdx(inviteeIdx);

        return input;
    }
}
