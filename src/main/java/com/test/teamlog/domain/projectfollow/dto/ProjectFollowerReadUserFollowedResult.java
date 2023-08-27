package com.test.teamlog.domain.projectfollow.dto;

import com.test.teamlog.entity.Project;
import com.test.teamlog.entity.ProjectFollower;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectFollowerReadUserFollowedResult {
    private Long id;
    private String masterId;
    private String name;
    private long postCount;
    private LocalDateTime updateTime; // 마지막 활동 시간
    private String updateTimeStr;
    private String thumbnail; // 대표 이미지

    public static ProjectFollowerReadUserFollowedResult of (ProjectFollower projectFollower) {
        final Project project = projectFollower.getProject();

        ProjectFollowerReadUserFollowedResult result = new ProjectFollowerReadUserFollowedResult();
        result.setId(project.getId());
        result.setMasterId(project.getMaster().getIdentification());
        result.setName(project.getName());
        result.setPostCount(project.getPosts().size());
        result.setUpdateTime(project.getUpdateTime());
        result.setUpdateTimeStr(project.getUpdateTime().toString());
        result.setThumbnail(project.getThumbnail());

        return result;
    }
}
