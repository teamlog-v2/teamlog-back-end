package com.app.teamlog.domain.projectapplication.dto;

import lombok.Data;

@Data
public class ProjectApplicationReadPendingResponse {
    private String applicantIdentification;
    private String applicantName;
    private String applicantProfileImgPath;

    public static ProjectApplicationReadPendingResponse from(ProjectApplicationReadPendingResult result) {
        ProjectApplicationReadPendingResponse response = new ProjectApplicationReadPendingResponse();
        response.setApplicantIdentification(result.getApplicantIdentification());
        response.setApplicantName(result.getApplicantName());
        response.setApplicantProfileImgPath(result.getApplicantProfileImgPath());

        return response;
    }
}
