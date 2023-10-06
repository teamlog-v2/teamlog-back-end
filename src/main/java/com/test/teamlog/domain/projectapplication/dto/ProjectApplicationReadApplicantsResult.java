package com.test.teamlog.domain.projectapplication.dto;

import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.domain.projectapplication.entity.ProjectApplication;
import lombok.Data;

@Data
public class ProjectApplicationReadApplicantsResult {
    private String applicantIdentification;
    private String applicantName;
    private String applicantProfileImgPath;

    public static ProjectApplicationReadApplicantsResult from(ProjectApplication projectApplication) {
        ProjectApplicationReadApplicantsResult result = new ProjectApplicationReadApplicantsResult();
        final User applicant = projectApplication.getApplicant();
        result.setApplicantIdentification(applicant.getIdentification());
        result.setApplicantName(applicant.getName());
        result.setApplicantProfileImgPath(applicant.getProfileImgPath());

        return result;
    }
}
