package com.app.teamlog.domain.projectfollow.dto;

import com.app.teamlog.domain.file.info.entity.FileInfo;
import com.app.teamlog.domain.project.entity.Project;
import com.app.teamlog.domain.projectfollow.entity.ProjectFollower;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectFollowerReadAccountFollowedResult {
    private Long id;
    private String masterId;
    private String name;
    private long postCount;
    private LocalDateTime updateTime; // 마지막 활동 시간
    private String updateTimeStr;
    private String thumbnail; // 대표 이미지

    public static ProjectFollowerReadAccountFollowedResult of (ProjectFollower projectFollower) {
        final Project project = projectFollower.getProject();

        ProjectFollowerReadAccountFollowedResult result = new ProjectFollowerReadAccountFollowedResult();
        result.setId(project.getId());
        result.setMasterId(project.getMaster().getIdentification());
        result.setName(project.getName());
        result.setPostCount(project.getPosts().size());
        result.setUpdateTime(project.getUpdateTime());
        result.setUpdateTimeStr(project.getUpdateTime().toString());

        final FileInfo thumbnail = project.getThumbnail();
        if (thumbnail != null) result.setThumbnail(thumbnail.getStoredFilePath());

        return result;
    }
}
