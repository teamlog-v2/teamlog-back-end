package com.app.teamlog.domain.projectapplication.dto;

import com.app.teamlog.domain.account.model.Account;
import com.app.teamlog.domain.project.entity.Project;
import com.app.teamlog.domain.projectapplication.entity.ProjectApplication;
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
