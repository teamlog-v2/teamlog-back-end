package com.app.teamlog.domain.projectjoin.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProjectJoinInviteRequest {
    private Long projectId;
    private List<String> accountIdList;

    public ProjectJoinInviteInput toInput() {
        ProjectJoinInviteInput input = new ProjectJoinInviteInput();
        input.setProjectId(this.projectId);
        input.setAccountIdentificationList(this.accountIdList);

        return input;
    }
}
