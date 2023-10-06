package com.test.teamlog.domain.projectapplication.dto;

import lombok.Data;

@Data
public class ProjectApplicationReadApplicantsResponse {
    private String applicantIdentification;
    private String applicantName;
    private String applicantProfileImgPath;

    public static ProjectApplicationReadApplicantsResponse from(ProjectApplicationReadApplicantsResult result) {
        ProjectApplicationReadApplicantsResponse response = new ProjectApplicationReadApplicantsResponse();
        response.setApplicantIdentification(result.getApplicantIdentification());
        response.setApplicantName(result.getApplicantName());
        response.setApplicantProfileImgPath(result.getApplicantProfileImgPath());

        return response;
    }
}
