package com.test.teamlog.domain.project.dto;

import com.test.teamlog.entity.Project;
import com.test.teamlog.entity.Team;
import com.test.teamlog.payload.TeamDTO;
import lombok.Data;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
    private TeamDTO.TeamSimpleInfo team;

    public void setTeam(Team team) {
        this.team = new TeamDTO.TeamSimpleInfo(team);
    }

    public static ProjectSearchResult from(Project project) {
        // FIXME: 추후 파일 관련된 부분은 일괄 수정
        final String imgUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/resources/")
                .path(project.getThumbnail())
                .toUriString();

        ProjectSearchResult result = new ProjectSearchResult();
        result.setId(project.getId());
        result.setName(project.getName());
        result.setPostCount(project.getPosts().size());
        result.setUpdateTime(project.getUpdateTime());
        result.setThumbnail(imgUri);

        return result;
    }
}
