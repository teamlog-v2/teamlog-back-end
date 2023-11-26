package com.test.teamlog.domain.project.dto;

import com.test.teamlog.domain.file.info.entity.FileInfo;
import com.test.teamlog.global.entity.AccessModifier;
import com.test.teamlog.domain.project.entity.Project;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectReadResult {
    private Long id;
    private String name;
    private String introduction;
    private AccessModifier accessModifier;
    private LocalDateTime createTime;
    private String masterId;
    private int memberCount;
    private int followerCount;

    private String thumbnail;
    private Relation relation;

    public static ProjectReadResult from(Project project) {
        ProjectReadResult response = new ProjectReadResult();
        response.setId(project.getId());
        response.setName(project.getName());
        response.setIntroduction(project.getIntroduction());
        response.setAccessModifier(project.getAccessModifier());
        response.setMasterId(project.getMaster().getIdentification());
        response.setMemberCount(project.getProjectMembers().size());
        response.setFollowerCount(project.getProjectFollowers().size());
        response.setCreateTime(project.getCreateTime());

        final FileInfo thumbnail = project.getThumbnail();
        if (thumbnail != null) response.setThumbnail(thumbnail.getStoredFilePath());

        return response;
    }
}
