package com.test.teamlog.payload;

import com.test.teamlog.entity.AccessModifier;
import com.test.teamlog.entity.Project;
import com.test.teamlog.entity.User;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ProjectDTO {
    @Getter
    public static class ProjectRequest {
        private String name;
        private String introduction;
        private AccessModifier accessModifier;
        private String masterId;
    }

    @Data
    public static class ProjectResponse {
        private Long id;
        private String name;
        private String introduction;
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
            this.masterId = project.getMaster().getId();
            this.memberCount = project.getProjectMembers().size();
            this.followerCount = project.getProjectFollowers().size();
            this.createTime = project.getCreateTime();
        }
    }

    @Data
    @Builder
    public static class ProjectListResponse {
        private Long id;
        private String name;
        private int postCount;
        private LocalDateTime updateTime; // 마지막 활동 시간
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
