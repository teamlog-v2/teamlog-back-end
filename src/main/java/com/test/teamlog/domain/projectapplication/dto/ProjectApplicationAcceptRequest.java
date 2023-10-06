package com.test.teamlog.domain.projectapplication.dto;

import lombok.Data;

@Data
public class ProjectApplicationAcceptRequest {
    private Long applicationIdx;

    public ProjectApplicationAcceptInput toInput(Long accountIdx) {
        ProjectApplicationAcceptInput input = new ProjectApplicationAcceptInput();
        input.setApplicationIdx(this.applicationIdx);
        input.setAccountIdx(accountIdx);

        return input;
    }
}
