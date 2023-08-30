package com.test.teamlog.domain.projectmember.dto;

import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.domain.projectmember.entity.ProjectMember;
import lombok.Data;

@Data
public class ProjectMemberReadResult {
    private String id;
    private String name;
    private String profileImgPath;

    public static ProjectMemberReadResult of(ProjectMember projectMember) {
        final User user = projectMember.getUser();

        ProjectMemberReadResult result = new ProjectMemberReadResult();
        result.setId(user.getIdentification());
        result.setName(user.getName());
        result.setProfileImgPath(user.getProfileImgPath());

        return result;
    }
}
