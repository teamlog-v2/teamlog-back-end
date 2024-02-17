package com.test.teamlog.domain.comment.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentCreateResponse {
    private Boolean isMyComment;
    private Long id;
    private String contents;
    private Integer childCommentCount;
    private AccountFollowInfoResponse writer;
    private List<String> commentMentions;
    private LocalDateTime writeTime;

    public static CommentCreateResponse of(CommentCreateResult result) {
        CommentCreateResponse response = new CommentCreateResponse();
        response.setIsMyComment(true);
        response.setId(result.getId());
        response.setContents(result.getContents());
        response.setChildCommentCount(result.getChildCommentCount());
        response.setWriter(AccountFollowInfoResponse.from(result.getWriter()));
        response.setCommentMentions(result.getCommentMentions());
        response.setWriteTime(result.getWriteTime());

        return response;
    }

    @Data
    static class AccountFollowInfoResponse {
        private String id;
        private String name;
        private String profileImgPath;

        public static AccountFollowInfoResponse from(CommentCreateResult.AccountSimpleInfoResult result) {
            AccountFollowInfoResponse accountFollowInfo = new AccountFollowInfoResponse();
            accountFollowInfo.setId(result.getId());
            accountFollowInfo.setName(result.getName());
            accountFollowInfo.setProfileImgPath(result.getProfileImgPath());

            return accountFollowInfo;
        }
    }
}
