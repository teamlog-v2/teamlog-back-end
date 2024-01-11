package com.test.teamlog.domain.projectjoin.dto;

import com.test.teamlog.domain.projectjoin.entity.ProjectJoin;
import lombok.Data;

@Data
public class ProjectJoinForUser {
    private Long id;
    private Long projectId;
    private String thumbnail; // 대표 이미지
    private String projectName;

    public static ProjectJoinForUser from(ProjectJoin projectJoin) {
        ProjectJoinForUser projectJoinForUser = new ProjectJoinForUser();
        projectJoinForUser.setId(projectJoin.getId());
        projectJoinForUser.setProjectId(projectJoin.getProject().getId());
        projectJoinForUser.setProjectName(projectJoin.getProject().getName());

        return projectJoinForUser;
    }

}