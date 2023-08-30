package com.test.teamlog.domain.comment.dto;

import com.test.teamlog.domain.account.dto.UserRequest;
import com.test.teamlog.domain.comment.entity.Comment;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentInfoResponse {
    private Boolean isMyComment;
    private Long id;
    private String contents;
    private UserRequest.UserSimpleInfo writer;
    private List<String> commentMentions;
    private LocalDateTime writeTime;

    public static CommentInfoResponse of(Comment comment) {
        CommentInfoResponse response = new CommentInfoResponse();
        response.setId(comment.getId());
        response.setContents(comment.getContents());
        response.setCommentMentions(comment.getCommentMentions().stream().map(c -> c.getTargetUser().getIdentification()).collect(Collectors.toList()));
        response.setWriteTime(comment.getCreateTime());

        return response;
    }
}
