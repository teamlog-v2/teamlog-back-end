package com.test.teamlog.domain.project.dto;

import com.test.teamlog.entity.AccessModifier;
import com.test.teamlog.entity.Project;
import com.test.teamlog.payload.Relation;
import com.test.teamlog.payload.TeamDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectCreateResult {
    private Relation relation;
    private Long id;
    private TeamDTO.TeamSimpleInfo team;
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
        response.setTeam(TeamDTO.TeamSimpleInfo.of(project.getTeam()));
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
