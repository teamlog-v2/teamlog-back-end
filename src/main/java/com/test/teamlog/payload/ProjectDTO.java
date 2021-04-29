package com.test.teamlog.payload;

import com.test.teamlog.entity.AccessModifier;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

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
    @Builder
    public static class ProjectResponse {
        private Long id;
        private String name;
        private String introduction;
        private AccessModifier accessModifier;
        private LocalDateTime createTime;
        private String masterId;
        private int memberCount;
        private int followerCount;
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
}
