package com.app.teamlog.domain.comment.dto;

import com.app.teamlog.domain.account.model.Account;
import com.app.teamlog.domain.comment.entity.Comment;
import com.app.teamlog.domain.post.entity.Post;
import lombok.Data;

import java.util.List;

@Data
public class CommentCreateInput {
    private Long parentCommentId;
    private Long postId;
    private String contents;
    private List<String> commentMentions;

    public Comment toComment(Account writer, Post post, Comment parentComment) {
        return Comment.builder()
                .writer(writer)
                .post(post)
                .contents(contents)
                .parentComment(parentComment)
                .build();
    }
}
