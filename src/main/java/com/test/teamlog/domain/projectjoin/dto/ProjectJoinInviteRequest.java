package com.test.teamlog.domain.projectjoin.dto;

import lombok.Data;

import java.util.List;

@Data
public class ProjectJoinInviteRequest {
    private Long projectId;
    private List<String> userIdList;

    public ProjectJoinInviteInput toInput() {
        ProjectJoinInviteInput input = new ProjectJoinInviteInput();
        input.setProjectId(this.projectId);
        input.setUserIdentificationList(this.userIdList);

        return input;
    }
}
