package com.test.teamlog.payload;

import com.test.teamlog.domain.account.dto.UserDTO;

import com.test.teamlog.entity.Comment;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class CommentDTO {
    @Getter
    public static class CommentRequest {
        private Long parentCommentId;
        private Long postId;
        private String contents;
        private List<String> commentMentions;
    }

    @Getter
    public static class CommentUpdateRequest {
        private String contents;
        private List<String> commentMentions;
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
        private Boolean isMyComment;
        private Long id;
        private String contents;
        private UserDTO.UserSimpleInfo writer;
        private List<String> commentMentions;
        private LocalDateTime writeTime;
    }

    @Data
    public static class CommentNotification {
        private Long commentId;
        private String contents;
        private String writeTime;
        private Long postId;
        public CommentNotification(Comment comment) {
            this.commentId = comment.getId();
            this.contents = comment.getContents();
            this.writeTime = comment.getCreateTime().toString();
            this.postId = comment.getPost().getId();
        }

    }
}
