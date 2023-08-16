package com.test.teamlog.domain.projectjoin.dto;

import lombok.Data;

@Data
public class ProjectJoinApplyRequest {
    private Long projectId;

    public ProjectJoinApplyInput toInput() {
        ProjectJoinApplyInput input = new ProjectJoinApplyInput();
        input.setProjectId(this.projectId);

        return input;
    }
}

