package com.test.teamlog.domain.project.dto;

import com.test.teamlog.entity.Project;
import com.test.teamlog.entity.Team;
import com.test.teamlog.payload.TeamDTO;
import lombok.Data;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;

@Data
public class ProjectReadByUserResult {
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

    public static ProjectReadByUserResult from(Project project) {
        String imgUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/resources/")
                .path(project.getThumbnail())
                .toUriString();

        ProjectReadByUserResult response = new ProjectReadByUserResult();
        response.setId(project.getId());
        response.setName(project.getName());
        response.setPostCount(project.getPosts().size());
        response.setUpdateTime(project.getUpdateTime());
        response.setThumbnail(imgUri);
        response.setTeam(project.getTeam());

        return response;
    }
}
