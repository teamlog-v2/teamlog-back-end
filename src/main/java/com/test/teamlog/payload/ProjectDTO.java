package com.test.teamlog.payload;

import com.test.teamlog.entity.AccessModifier;
import com.test.teamlog.entity.Project;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

public class ProjectDTO {
    @Getter
    public static class ProjectRequest {
        @NotBlank(message = "빈문자열, 공백만 입력할 수 없습니다.")
        @Size(min = 1, max = 20, message = "프로젝트 이름을 1자에서 20자 사이로 입력해주세요.")
        private String name;
        private String introduction;
        private AccessModifier accessModifier;
        private String masterId;
        private Long teamId;
    }

    @Data
    public static class ProjectResponse {
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

        public ProjectResponse(Project project) {
            this.id = project.getId();
            this.name = project.getName();
            this.introduction = project.getIntroduction();
            this.accessModifier = project.getAccessModifier();
            this.masterId = project.getMaster().getIdentification();
            this.memberCount = project.getProjectMembers().size();
            this.followerCount = project.getProjectFollowers().size();
            this.createTime = project.getCreateTime();
        }
    }

    @Data
    @Builder
    public static class ProjectListResponse {
        private Long id;
        private String masterId;
        private String name;
        private long postCount;
        private LocalDateTime updateTime; // 마지막 활동 시간
        private String updateTimeStr;
        private String thumbnail; // 대표 이미지
    }

    @Data
    public static class ProjectSimpleInfo {
        private Long id;
        private String name;

        public ProjectSimpleInfo(Project project) {
            this.id = project.getId();
            this.name = project.getName();
        }
    }
}
