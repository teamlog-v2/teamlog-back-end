package com.test.teamlog.domain.projectapplication.dto;

import com.test.teamlog.domain.account.model.Account;
import com.test.teamlog.domain.project.entity.Project;
import com.test.teamlog.domain.projectapplication.entity.ProjectApplication;
import lombok.Data;

@Data
public class ProjectApplicationCreateInput {
    private Long projectIdx;
    private Long applicantIdx;

    public ProjectApplication toProjectApplication(Project project, Account applicant) {
        return ProjectApplication.builder()
                .project(project)
                .applicant(applicant)
                .build();
    }
}
