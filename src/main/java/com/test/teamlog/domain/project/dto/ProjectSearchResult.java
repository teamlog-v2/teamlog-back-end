package com.test.teamlog.domain.project.dto;

import com.test.teamlog.domain.file.info.entity.FileInfo;
import com.test.teamlog.domain.project.entity.Project;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectSearchResult {
    private Long id;
    private String masterId;
    private String name;
    private long postCount;
    private LocalDateTime updateTime; // 마지막 활동 시간
    private String updateTimeStr;
    private String thumbnail; // 대표 이미지

    public static ProjectSearchResult from(Project project) {
        ProjectSearchResult result = new ProjectSearchResult();
        result.setId(project.getId());
        result.setName(project.getName());
        result.setPostCount(project.getPosts().size());
        result.setUpdateTime(project.getUpdateTime());

        final FileInfo thumbnail = project.getThumbnail();
        if (thumbnail != null) result.setThumbnail(thumbnail.getStoredFilePath());

        return result;
    }
}
