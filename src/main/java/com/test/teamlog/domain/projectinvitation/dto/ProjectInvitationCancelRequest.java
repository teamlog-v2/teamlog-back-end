package com.test.teamlog.domain.projectinvitation.dto;

import lombok.Data;

@Data
public class ProjectInvitationCancelRequest {
    private Long projectIdx;
    private Long inviteeIdx;

    public ProjectInvitationCancelInput toInput(Long inviterIdx) {
        ProjectInvitationCancelInput input = new ProjectInvitationCancelInput();
        input.setProjectIdx(this.projectIdx);
        input.setInviterIdx(inviterIdx);
        input.setInviteeIdx(this.inviteeIdx);

        return input;
    }
}
