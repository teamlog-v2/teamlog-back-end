package com.test.teamlog.domain.project.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectSearchResponse {
    private Long id;
    private String masterId;
    private String name;
    private long postCount;
    private LocalDateTime updateTime; // 마지막 활동 시간
    private String updateTimeStr;
    private String thumbnail; // 대표 이미지

    public static ProjectSearchResponse from(ProjectSearchResult result) {
        ProjectSearchResponse response = new ProjectSearchResponse();
        response.setId(result.getId());
        response.setName(result.getName());
        response.setPostCount(result.getPostCount());
        response.setUpdateTime(result.getUpdateTime());
        response.setThumbnail(result.getThumbnail());

        return response;
    }
}
