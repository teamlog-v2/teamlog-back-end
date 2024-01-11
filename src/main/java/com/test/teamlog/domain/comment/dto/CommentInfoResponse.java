package com.test.teamlog.domain.comment.dto;

import com.test.teamlog.domain.account.model.User;
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
    private Integer childCommentCount;
    private UserSimpleInfoResult writer;
    private List<String> commentMentions;
    private LocalDateTime writeTime;

    public static CommentInfoResponse from(Comment comment) {
        CommentInfoResponse response = new CommentInfoResponse();
        response.setId(comment.getId());
        response.setContents(comment.getContents());
        response.setChildCommentCount(comment.getChildComments().size());
        response.setCommentMentions(comment.getCommentMentions().stream().map(c -> c.getTargetUser().getIdentification()).collect(Collectors.toList()));
        response.setWriteTime(comment.getCreateTime());
        response.setWriter(UserSimpleInfoResult.from(comment.getWriter()));

        return response;
    }

    @Data
    static class UserSimpleInfoResult {
        private String id;
        private String name;
        private String profileImgPath;

        public static UserSimpleInfoResult from(User user) {
            UserSimpleInfoResult userFollowInfo = new UserSimpleInfoResult();
            userFollowInfo.setId(user.getIdentification());
            userFollowInfo.setName(user.getName());
            userFollowInfo.setProfileImgPath(user.getProfileImage().getStoredFilePath());

            return userFollowInfo;
        }
    }
}
