package com.test.teamlog.domain.project.dto;

import com.test.teamlog.entity.Team;
import com.test.teamlog.payload.TeamDTO;
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
    private TeamDTO.TeamSimpleInfo team;

    public void setTeam(Team team) {
        this.team = TeamDTO.TeamSimpleInfo.of(team);
    }

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
