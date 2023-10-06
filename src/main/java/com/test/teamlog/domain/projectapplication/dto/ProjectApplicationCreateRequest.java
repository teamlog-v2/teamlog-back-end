package com.test.teamlog.domain.projectapplication.dto;

import lombok.Data;

@Data
public class ProjectApplicationCreateRequest {
    private Long projectIdx;
    private Long applicantIdx;

    public ProjectApplicationCreateInput toInput(Long applicantIdx) {
        ProjectApplicationCreateInput input = new ProjectApplicationCreateInput();
        input.setProjectIdx(this.projectIdx);
        input.setApplicantIdx(applicantIdx);

        return input;
    }
}
