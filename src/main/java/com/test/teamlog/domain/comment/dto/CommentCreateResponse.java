package com.test.teamlog.domain.comment.dto;

import com.test.teamlog.domain.account.dto.UserRequest;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentCreateResponse {
    private Boolean isMyComment;
    private Long id;
    private String contents;
    private Integer childCommentCount;
    private UserRequest.UserSimpleInfo writer;
    private List<String> commentMentions;
    private LocalDateTime writeTime;

    public static CommentCreateResponse of(CommentCreateResult result) {
        CommentCreateResponse response = new CommentCreateResponse();
        response.setIsMyComment(true);
        response.setId(result.getId());
        response.setContents(result.getContents());
        response.setChildCommentCount(result.getChildCommentCount());
        response.setWriter(result.getWriter());
        response.setCommentMentions(result.getCommentMentions());
        response.setWriteTime(result.getWriteTime());

        return response;
    }
}
