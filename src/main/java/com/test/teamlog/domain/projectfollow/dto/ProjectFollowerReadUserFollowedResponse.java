package com.test.teamlog.domain.projectfollow.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectFollowerReadUserFollowedResponse {
    private Long id;
    private String masterId;
    private String name;
    private long postCount;
    private LocalDateTime updateTime; // 마지막 활동 시간
    private String updateTimeStr;
    private String thumbnail; // 대표 이미지

    public static ProjectFollowerReadUserFollowedResponse of(ProjectFollowerReadUserFollowedResult result) {
        ProjectFollowerReadUserFollowedResponse response = new ProjectFollowerReadUserFollowedResponse();
        response.setId(result.getId());
        response.setMasterId(result.getMasterId());
        response.setName(result.getName());
        response.setPostCount(result.getPostCount());
        response.setUpdateTime(result.getUpdateTime());
        response.setUpdateTimeStr(result.getUpdateTime().toString());
        response.setThumbnail(result.getThumbnail());

        return response;
    }
}
