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
    private UserFollowInfoResponse writer;
    private List<String> commentMentions;
    private LocalDateTime writeTime;

    public static CommentCreateResponse of(CommentCreateResult result) {
        CommentCreateResponse response = new CommentCreateResponse();
        response.setIsMyComment(true);
        response.setId(result.getId());
        response.setContents(result.getContents());
        response.setChildCommentCount(result.getChildCommentCount());
        response.setWriter(UserFollowInfoResponse.from(result.getWriter()));
        response.setCommentMentions(result.getCommentMentions());
        response.setWriteTime(result.getWriteTime());

        return response;
    }

    @Data
    static class UserFollowInfoResponse {
        private String id;
        private String name;
        private String profileImgPath;

        public static UserFollowInfoResponse from(CommentCreateResult.UserSimpleInfoResult result) {
            UserFollowInfoResponse userFollowInfo = new UserFollowInfoResponse();
            userFollowInfo.setId(result.getId());
            userFollowInfo.setName(result.getName());
            userFollowInfo.setProfileImgPath(result.getProfileImgPath());

            return userFollowInfo;
        }
    }
}
