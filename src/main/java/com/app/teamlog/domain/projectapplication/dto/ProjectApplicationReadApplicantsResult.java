package com.app.teamlog.domain.projectapplication.dto;

import com.app.teamlog.domain.account.model.Account;
import com.app.teamlog.domain.file.info.entity.FileInfo;
import com.app.teamlog.domain.projectapplication.entity.ProjectApplication;
import lombok.Data;

@Data
public class ProjectApplicationReadApplicantsResult {
    private String applicantIdentification;
    private String applicantName;
    private String applicantProfileImgPath;

    public static ProjectApplicationReadApplicantsResult from(ProjectApplication projectApplication) {
        ProjectApplicationReadApplicantsResult result = new ProjectApplicationReadApplicantsResult();
        final Account applicant = projectApplication.getApplicant();
        result.setApplicantIdentification(applicant.getIdentification());
        result.setApplicantName(applicant.getName());

        final FileInfo profileImage = applicant.getProfileImage();
        if (profileImage != null) result.setApplicantIdentification(profileImage.getStoredFilePath());

        return result;
    }
}
