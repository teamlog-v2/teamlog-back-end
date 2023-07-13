package com.test.teamlog.domain.comment.dto;

import lombok.Data;

import java.util.List;

@Data
public class CommentCreateRequest {
    private Long parentCommentId;
    private Long postId;
    private String contents;
    private List<String> commentMentions;

    public CommentCreateInput toInput() {
        CommentCreateInput input = new CommentCreateInput();
        input.setParentCommentId(parentCommentId);
        input.setPostId(postId);
        input.setContents(contents);
        input.setCommentMentions(commentMentions);

        return input;
    }
}
