package com.test.teamlog.domain.projectjoin.dto;

import com.test.teamlog.domain.account.model.Account;
import com.test.teamlog.domain.projectjoin.entity.ProjectJoin;
import lombok.Data;

@Data
public class ProjectJoinForProject {
    private Long id;
    private String projectName;
    private AccountSimpleInfoResponse account;

    public static ProjectJoinForProject from(ProjectJoin projectJoin) {
        ProjectJoinForProject projectJoinForProject = new ProjectJoinForProject();
        projectJoinForProject.setId(projectJoin.getId());
        projectJoinForProject.setProjectName(projectJoin.getProject().getName());
        projectJoinForProject.setAccount(AccountSimpleInfoResponse.from(projectJoin.getAccount()));

        return projectJoinForProject;
    }

    @Data
    static class AccountSimpleInfoResponse {
        private String id;
        private String name;
        private String profileImgPath;

        static AccountSimpleInfoResponse from(Account account) {
            final AccountSimpleInfoResponse accountSimpleInfoResponse = new AccountSimpleInfoResponse();
            accountSimpleInfoResponse.setId(account.getIdentification());
            accountSimpleInfoResponse.setName(account.getName());
            if (account.getProfileImage() != null) {
                accountSimpleInfoResponse.setProfileImgPath(account.getProfileImage().getStoredFilePath());
            }

            return accountSimpleInfoResponse;
        }
    }
}