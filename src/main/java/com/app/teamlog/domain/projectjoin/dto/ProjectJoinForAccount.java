package com.app.teamlog.domain.projectjoin.dto;

import com.app.teamlog.domain.projectjoin.entity.ProjectJoin;
import lombok.Data;

@Data
public class ProjectJoinForAccount {
    private Long id;
    private Long projectId;
    private String thumbnail; // 대표 이미지
    private String projectName;

    public static ProjectJoinForAccount from(ProjectJoin projectJoin) {
        ProjectJoinForAccount projectJoinForAccount = new ProjectJoinForAccount();
        projectJoinForAccount.setId(projectJoin.getId());
        projectJoinForAccount.setProjectId(projectJoin.getProject().getId());
        projectJoinForAccount.setProjectName(projectJoin.getProject().getName());

        return projectJoinForAccount;
    }

}