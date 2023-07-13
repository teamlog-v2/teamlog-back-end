package com.test.teamlog.domain.comment.dto;

import com.test.teamlog.entity.Comment;
import lombok.Data;

public class CommentDTO {
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
