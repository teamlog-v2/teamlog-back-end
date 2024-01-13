package com.test.teamlog.domain.projectjoin.dto;

import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.domain.projectjoin.entity.ProjectJoin;
import lombok.Data;

@Data
public class ProjectJoinForProject {
    private Long id;
    private String projectName;
    private UserSimpleInfoResponse user;

    public static ProjectJoinForProject from(ProjectJoin projectJoin) {
        ProjectJoinForProject projectJoinForProject = new ProjectJoinForProject();
        projectJoinForProject.setId(projectJoin.getId());
        projectJoinForProject.setProjectName(projectJoin.getProject().getName());
        projectJoinForProject.setUser(UserSimpleInfoResponse.from(projectJoin.getUser()));

        return projectJoinForProject;
    }

    @Data
    static class UserSimpleInfoResponse {
        private String id;
        private String name;
        private String profileImgPath;

        static UserSimpleInfoResponse from(User user) {
            final UserSimpleInfoResponse userSimpleInfoResponse = new UserSimpleInfoResponse();
            userSimpleInfoResponse.setId(user.getIdentification());
            userSimpleInfoResponse.setName(user.getName());
            if (user.getProfileImage() != null) {
                userSimpleInfoResponse.setProfileImgPath(user.getProfileImage().getStoredFilePath());
            }

            return userSimpleInfoResponse;
        }
    }
}