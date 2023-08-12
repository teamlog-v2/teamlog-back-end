package com.test.teamlog.domain.project.dto;

import com.test.teamlog.entity.AccessModifier;
import com.test.teamlog.payload.Relation;
import com.test.teamlog.payload.TeamDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectReadResponse {
    private Long id;
    private TeamDTO.TeamSimpleInfo team;
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
        response.setTeam(result.getTeam());
        response.setName(result.getName());
        response.setIntroduction(result.getIntroduction());
        response.setAccessModifier(result.getAccessModifier());
        response.setMasterId(result.getMasterId());
        response.setMemberCount(result.getMemberCount());
        response.setFollowerCount(result.getFollowerCount());
        response.setCreateTime(result.getCreateTime());

        return response;
    }
}
