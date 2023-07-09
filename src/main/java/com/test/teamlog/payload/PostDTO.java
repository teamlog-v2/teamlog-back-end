package com.test.teamlog.payload;

import com.test.teamlog.domain.account.dto.UserRequest;

import com.test.teamlog.entity.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class PostDTO {

    @Data
    @Builder
    public static class PostResponse {
        private Boolean isILikeIt;
        private Long id;
        private ProjectDTO.ProjectSimpleInfo project;
        private UserRequest.UserSimpleInfo writer;
        private AccessModifier accessModifier;
        private AccessModifier commentModifier;
        private String contents;
        private Double latitude;
        private Double longitude;
        private String address;
        private int likeCount;
        private int commentCount;
        private LocalDateTime writeTime;
        private String writeTimeStr;
        private List<String> hashtags;
        private List<FileDTO.FileInfo> media;
        private List<FileDTO.FileInfo> files;
    }

    @Getter
    public static class PostHistoryInfo {
        private UserRequest.UserSimpleInfo writer;
        private LocalDateTime writeTime;
        public PostHistoryInfo(PostUpdateHistory history) {
            this.writer = new UserRequest.UserSimpleInfo(history.getUser());
            this.writeTime = history.getCreateTime();
        }
    }
}
