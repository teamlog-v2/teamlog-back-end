package com.test.teamlog.payload;

import com.test.teamlog.entity.AccessModifier;
import com.test.teamlog.entity.Team;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class TeamDTO {
    @Getter
    public static class TeamRequest {
        @NotBlank(message = "빈문자열, 공백만 입력할 수 없습니다.")
        @Size(min=1,max=20, message = "팀 이름을 1자에서 20자 사이로 입력해주세요.")
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
        private int followerCount;
        public TeamResponse(Team team) {
            this.id = team.getId();
            this.name = team.getName();
            this.introduction = team.getIntroduction();
            this.accessModifier = team.getAccessModifier();
            this.masterId = team.getMaster().getId();
            this.memberCount = team.getTeamMembers().size();
            this.followerCount = team.getTeamFollowers().size();
            this.createTime = team.getCreateTime();
        }
    }

    @Data
    @Builder
    public static class TeamListResponse {
        private Long id;
        private String masterId;
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
