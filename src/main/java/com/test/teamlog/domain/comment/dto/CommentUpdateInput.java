package com.test.teamlog.domain.comment.dto;

import lombok.Data;

import java.util.List;

@Data
public class CommentUpdateInput {
    private String contents;
    private List<String> commentMentions;
}