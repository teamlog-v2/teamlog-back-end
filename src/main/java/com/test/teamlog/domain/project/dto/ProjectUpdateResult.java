package com.test.teamlog.domain.project.dto;

import com.test.teamlog.global.entity.AccessModifier;
import com.test.teamlog.domain.project.entity.Project;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectUpdateResult {
    private Relation relation;
    private Long id;
    private String name;
    private String introduction;
    private String thumbnail;
    private AccessModifier accessModifier;
    private LocalDateTime createTime;
    private String masterId;
    private int memberCount;
    private int followerCount;

    public static ProjectUpdateResult of(Project project, Relation relation) {
        ProjectUpdateResult response = new ProjectUpdateResult();
        response.setRelation(relation);
        response.setId(project.getId());
        response.setName(project.getName());
        response.setIntroduction(project.getIntroduction());
        response.setThumbnail(project.getThumbnail());
        response.setAccessModifier(project.getAccessModifier());
        response.setCreateTime(project.getCreateTime());
        response.setMasterId(project.getMaster().getIdentification());
        response.setMemberCount(project.getProjectMembers().size());
        response.setFollowerCount(project.getProjectFollowers().size());

        return response;
    }
}
