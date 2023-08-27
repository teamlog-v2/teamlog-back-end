package com.test.teamlog.domain.projectmember.dto;

import lombok.Data;

@Data
public class ProjectMemberReadResponse {
    private String id;
    private String name;
    private String profileImgPath;

    public static ProjectMemberReadResponse of(ProjectMemberReadResult result) {
        ProjectMemberReadResponse response = new ProjectMemberReadResponse();
        response.setId(result.getId());
        response.setName(result.getName());
        response.setProfileImgPath(result.getProfileImgPath());

        return response;
    }
}
