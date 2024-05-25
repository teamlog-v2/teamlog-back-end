package com.app.teamlog.domain.projectinvitation.dto;

import lombok.Data;

@Data
public class ProjectInvitationCreateRequest {
    private Long projectIdx;
    private Long inviteeIdx;

    public ProjectInvitationCreateInput toInput(Long inviterIdx) {
        ProjectInvitationCreateInput input = new ProjectInvitationCreateInput();
        input.setProjectIdx(this.projectIdx);
        input.setInviterIdx(inviterIdx);
        input.setInviteeIdx(this.inviteeIdx);

        return input;
    }
}
