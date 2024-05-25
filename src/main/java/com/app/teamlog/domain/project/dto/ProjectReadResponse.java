package com.app.teamlog.domain.project.dto;

import com.app.teamlog.global.entity.AccessModifier;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectReadResponse {
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

    public static ProjectReadResponse from(ProjectReadResult result) {
        ProjectReadResponse response = new ProjectReadResponse();
        response.setId(result.getId());
        response.setName(result.getName());
        response.setIntroduction(result.getIntroduction());
        response.setAccessModifier(result.getAccessModifier());
        response.setMasterId(result.getMasterId());
        response.setMemberCount(result.getMemberCount());
        response.setFollowerCount(result.getFollowerCount());
        response.setCreateTime(result.getCreateTime());
        response.setThumbnail(result.getThumbnail());
        response.setRelation(result.getRelation());

        return response;
    }
}
