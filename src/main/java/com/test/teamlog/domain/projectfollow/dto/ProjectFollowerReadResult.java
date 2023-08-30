package com.test.teamlog.domain.projectfollow.dto;

import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.domain.projectfollow.entity.ProjectFollower;
import lombok.Data;

@Data
public class ProjectFollowerReadResult {
    private String id;
    private String name;
    private String profileImgPath;

    public static ProjectFollowerReadResult of(ProjectFollower projectFollower) {
        final User user = projectFollower.getUser();

        ProjectFollowerReadResult result = new ProjectFollowerReadResult();
        result.setId(user.getIdentification());
        result.setName(user.getName());
        result.setProfileImgPath(user.getProfileImgPath());

        return result;
    }
}
