package com.test.teamlog.domain.project.dto;

import com.test.teamlog.domain.project.entity.Project;
import lombok.Data;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;

@Data
public class ProjectReadUserFollowingResult {
    private Long id;
    private String masterId;
    private String name;
    private long postCount;
    private LocalDateTime updateTime; // 마지막 활동 시간
    private String updateTimeStr;
    private String thumbnail; // 대표 이미지

    public static ProjectReadUserFollowingResult from(Project project) {
        String imgUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/resources/")
                .path(project.getThumbnail())
                .toUriString();

        ProjectReadUserFollowingResult response = new ProjectReadUserFollowingResult();
        response.setId(project.getId());
        response.setName(project.getName());
        response.setPostCount(project.getPosts().size());
        response.setUpdateTime(project.getUpdateTime());
        response.setThumbnail(imgUri);

        return response;
    }
}
