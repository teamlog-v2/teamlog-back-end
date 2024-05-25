package com.app.teamlog.domain.projectfollow.dto;

import com.app.teamlog.domain.account.model.Account;
import com.app.teamlog.domain.file.info.entity.FileInfo;
import com.app.teamlog.domain.projectfollow.entity.ProjectFollower;
import lombok.Data;

@Data
public class ProjectFollowerReadResult {
    private String id;
    private String name;
    private String profileImgPath;

    public static ProjectFollowerReadResult of(ProjectFollower projectFollower) {
        final Account account = projectFollower.getAccount();

        ProjectFollowerReadResult result = new ProjectFollowerReadResult();
        result.setId(account.getIdentification());
        result.setName(account.getName());

        final FileInfo profileImage = account.getProfileImage();
        if (profileImage != null) result.setProfileImgPath(profileImage.getStoredFilePath());

        return result;
    }
}
