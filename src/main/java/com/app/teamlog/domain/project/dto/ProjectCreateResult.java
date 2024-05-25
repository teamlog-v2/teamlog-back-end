package com.app.teamlog.domain.project.dto;

import com.app.teamlog.domain.file.info.entity.FileInfo;
import com.app.teamlog.global.entity.AccessModifier;
import com.app.teamlog.domain.project.entity.Project;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectCreateResult {
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

    public static ProjectCreateResult of(Project project, Relation relation) {
        ProjectCreateResult response = new ProjectCreateResult();
        response.setRelation(relation);
        response.setId(project.getId());
        response.setName(project.getName());
        response.setIntroduction(project.getIntroduction());
        response.setAccessModifier(project.getAccessModifier());
        response.setCreateTime(project.getCreateTime());
        response.setMasterId(project.getMaster().getIdentification());
        response.setMemberCount(project.getProjectMembers().size());
        response.setFollowerCount(project.getProjectFollowers().size());

        final FileInfo thumbnail = project.getThumbnail();
        if (thumbnail != null) response.setThumbnail(thumbnail.getStoredFilePath());

        return response;
    }
}
