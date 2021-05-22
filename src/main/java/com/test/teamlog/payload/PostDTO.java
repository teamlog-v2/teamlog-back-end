package com.test.teamlog.payload;

import com.test.teamlog.entity.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class PostDTO {
    @Getter
    public static class PostRequest {
        private String contents;
        private AccessModifier accessModifier;
        private AccessModifier commentModifier;
        private Double latitude;
        private Double longitude;
        private String address;
        private Long projectId;
        private List<String> hashtags;
    }

    @Getter
    public static class PostUpdateRequest {
        private String contents;
        private AccessModifier accessModifier;
        private AccessModifier commentModifier;
        private Double latitude;
        private Double longitude;
        private String address;
        private Long projectId;
        private List<String> hashtags;
        private List<Long> deletedFileIdList;
    }

    @Data
    @Builder
    public static class PostResponse {
        private Boolean isILikeIt;
        private Long id;
        private ProjectDTO.ProjectSimpleInfo project;
        private UserDTO.UserSimpleInfo writer;
        private AccessModifier accessModifier;
        private AccessModifier commentModifier;
        private String contents;
        private Double latitude;
        private Double longitude;
        private String address;
        private int likeCount;
        private int commentCount;
        private LocalDateTime writeTime;
        private List<String> hashtags;
        private List<FileDTO.FileInfo> media;
        private List<FileDTO.FileInfo> files;
    }

    @Getter
    public static class PostHistoryInfo {
        private UserDTO.UserSimpleInfo writer;
        private LocalDateTime writeTime;
        public PostHistoryInfo(PostUpdateHistory history) {
            this.writer = new UserDTO.UserSimpleInfo(history.getUser());
            this.writeTime = history.getCreateTime();
        }
    }
}
