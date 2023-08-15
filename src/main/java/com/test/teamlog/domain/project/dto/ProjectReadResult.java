package com.test.teamlog.domain.project.dto;

import com.test.teamlog.entity.AccessModifier;
import com.test.teamlog.entity.Project;
import com.test.teamlog.payload.Relation;
import com.test.teamlog.payload.TeamDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectReadResult {
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

    public static ProjectReadResult from(Project project) {
        ProjectReadResult response = new ProjectReadResult();
        response.setId(project.getId());
        if(project.getTeam() != null) {
            response.setTeam(TeamDTO.TeamSimpleInfo.of(project.getTeam()));
        }
        response.setName(project.getName());
        response.setIntroduction(project.getIntroduction());
        response.setAccessModifier(project.getAccessModifier());
        response.setMasterId(project.getMaster().getIdentification());
        response.setMemberCount(project.getProjectMembers().size());
        response.setFollowerCount(project.getProjectFollowers().size());
        response.setCreateTime(project.getCreateTime());

        return response;
    }
}
