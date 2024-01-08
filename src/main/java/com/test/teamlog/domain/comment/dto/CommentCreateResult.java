package com.test.teamlog.domain.comment.dto;

import com.test.teamlog.domain.account.dto.UserRequest;
import com.test.teamlog.domain.comment.entity.Comment;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class CommentCreateResult {
    private Boolean isMyComment;
    private Long id;
    private String contents;
    private Integer childCommentCount;
    private UserRequest.UserSimpleInfo writer;
    private List<String> commentMentions;
    private LocalDateTime writeTime;

    public static CommentCreateResult of(Comment comment) {
        CommentCreateResult result = new CommentCreateResult();
        result.setIsMyComment(true);
        result.setId(comment.getId());
        result.setContents(comment.getContents());
        result.setChildCommentCount(comment.getChildComments().size());
        result.setWriter(new UserRequest.UserFollowInfo(comment.getWriter()));
        result.setCommentMentions(comment.getCommentMentions().stream().map(c -> c.getTargetUser().getIdentification()).collect(Collectors.toList()));
        result.setWriteTime(comment.getCreateTime());

        return result;
    }
}
