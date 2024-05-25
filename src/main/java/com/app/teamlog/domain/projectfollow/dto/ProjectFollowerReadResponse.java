package com.app.teamlog.domain.projectfollow.dto;

import lombok.Data;

@Data
public class ProjectFollowerReadResponse {
    private String id;
    private String name;
    private String profileImgPath;

    public static ProjectFollowerReadResponse of(ProjectFollowerReadResult result) {
        ProjectFollowerReadResponse response = new ProjectFollowerReadResponse();
        response.setId(result.getId());
        response.setName(result.getName());
        response.setProfileImgPath(result.getProfileImgPath());

        return response;
    }
}
