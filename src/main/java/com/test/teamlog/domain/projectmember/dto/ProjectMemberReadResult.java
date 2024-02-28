package com.test.teamlog.domain.projectmember.dto;

import com.test.teamlog.domain.account.model.Account;
import com.test.teamlog.domain.file.info.entity.FileInfo;
import com.test.teamlog.domain.projectmember.entity.ProjectMember;
import lombok.Data;

@Data
public class ProjectMemberReadResult {
    private String id;
    private String name;
    private String profileImgPath;

    public static ProjectMemberReadResult of(ProjectMember projectMember) {
        final Account account = projectMember.getAccount();

        ProjectMemberReadResult result = new ProjectMemberReadResult();
        result.setId(account.getIdentification());
        result.setName(account.getName());

        final FileInfo profileImage = account.getProfileImage();
        if (profileImage != null) result.setProfileImgPath(profileImage.getStoredFilePath());

        return result;
    }
}
