package com.test.teamlog.domain.projectinvitation.dto;

import lombok.Data;

@Data
public class ProjectInvitationCreateRequest {
    private Long projectIdx;
    private Long inviteeIdx;

    public ProjectInvitationCreateInput toInput(Long invitorIdx) {
        ProjectInvitationCreateInput input = new ProjectInvitationCreateInput();
        input.setProjectIdx(this.projectIdx);
        input.setInviterIdx(invitorIdx);
        input.setInviteeIdx(this.inviteeIdx);

        return input;
    }
}
