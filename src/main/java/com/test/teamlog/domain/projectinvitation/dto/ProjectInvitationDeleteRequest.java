package com.test.teamlog.domain.projectinvitation.dto;

import lombok.Data;

@Data
public class ProjectInvitationDeleteRequest {
    private Long projectIdx;
    private Long inviteeIdx;

    public ProjectInvitationDeleteInput toInput(Long invitorIdx) {
        ProjectInvitationDeleteInput input = new ProjectInvitationDeleteInput();
        input.setProjectIdx(this.projectIdx);
        input.setInviterIdx(invitorIdx);
        input.setInviteeIdx(this.inviteeIdx);

        return input;
    }
}
