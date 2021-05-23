package com.test.teamlog.payload;

import com.test.teamlog.entity.AccessModifier;
import com.test.teamlog.entity.Team;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

public class TeamDTO {
    @Getter
    public static class TeamRequest {
        private String name;
        private String introduction;
        private AccessModifier accessModifier;
        private String masterId;
    }

    @Data
    public static class TeamResponse {
        private Relation relation;
        private Long id;
        private String name;
        private String introduction;
        private AccessModifier accessModifier;
        private LocalDateTime createTime;
        private String masterId;
        private int memberCount;
        public TeamResponse(Team team) {
            this.id = team.getId();
            this.name = team.getName();
            this.introduction = team.getIntroduction();
            this.accessModifier = team.getAccessModifier();
            this.masterId = team.getMaster().getId();
            this.memberCount = team.getTeamMembers().size();
            this.createTime = team.getCreateTime();
        }
    }

    @Data
    @Builder
    public static class TeamListResponse {
        private Long id;
        private String name;
        private long projectCount;
        private LocalDateTime updateTime;
    }

    @Data
    public static class TeamSimpleInfo {
        private Long id;
        private String name;
        public TeamSimpleInfo(Team team) {
            this.id = team.getId();
            this.name = team.getName();
        }
    }
}
