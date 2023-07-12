package com.test.teamlog.domain.comment.dto;

import lombok.Data;

import java.util.List;

@Data
public class CommentUpdateRequest {
    private String contents;
    private List<String> commentMentions;

    public CommentUpdateInput toInput() {
        CommentUpdateInput input = new CommentUpdateInput();
        input.setContents(contents);
        input.setCommentMentions(commentMentions);

        return input;
    }
}