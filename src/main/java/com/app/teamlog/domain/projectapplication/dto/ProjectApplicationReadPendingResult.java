package com.app.teamlog.domain.projectapplication.dto;

import com.app.teamlog.domain.account.model.Account;
import com.app.teamlog.domain.file.info.entity.FileInfo;
import com.app.teamlog.domain.projectapplication.entity.ProjectApplication;
import lombok.Data;

@Data
public class ProjectApplicationReadPendingResult {
    private String applicantIdentification;
    private String applicantName;
    private String applicantProfileImgPath;

    public static ProjectApplicationReadPendingResult from(ProjectApplication projectApplication) {
        ProjectApplicationReadPendingResult result = new ProjectApplicationReadPendingResult();
        final Account applicant = projectApplication.getApplicant();
        result.setApplicantIdentification(applicant.getIdentification());
        result.setApplicantName(applicant.getName());

        final FileInfo profileImage = applicant.getProfileImage();
        if (profileImage != null) result.setApplicantProfileImgPath(profileImage.getStoredFilePath());

        return result;
    }
}
