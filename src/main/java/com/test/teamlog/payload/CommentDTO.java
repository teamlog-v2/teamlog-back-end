package com.test.teamlog.payload;

import com.test.teamlog.entity.TaskStatus;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class CommentDTO {
    @Getter
    public static class CommentRequest {
        private Long parentCommentId;
        private String writerId;
        private Long postId;
        private String contents;
    }

    @Data
    @Builder
    public static class CommentResponse {
        private Long id;
        private String contents;
        private UserDTO.UserSimpleInfo writer;
        private List<String> commentMentions;
        private List<CommentInfo> childComments;
        private LocalDateTime writeTime;
    }

    @Data
    @Builder
    public static class CommentInfo {
        private Long id;
        private String contents;
        private UserDTO.UserSimpleInfo writer;
        private List<String> commentMentions;
        private LocalDateTime writeTime;
    }
}
