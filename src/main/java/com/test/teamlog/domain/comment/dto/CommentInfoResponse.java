package com.test.teamlog.domain.comment.dto;

import com.test.teamlog.domain.account.model.Account;
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
    private AccountSimpleInfoResult writer;
    private List<String> commentMentions;
    private LocalDateTime writeTime;

    public static CommentInfoResponse from(Comment comment) {
        CommentInfoResponse response = new CommentInfoResponse();
        response.setId(comment.getId());
        response.setContents(comment.getContents());
        response.setChildCommentCount(comment.getChildComments().size());
        response.setCommentMentions(comment.getCommentMentions().stream().map(c -> c.getTargetAccount().getIdentification()).collect(Collectors.toList()));
        response.setWriteTime(comment.getCreateTime());
        response.setWriter(AccountSimpleInfoResult.from(comment.getWriter()));

        return response;
    }

    @Data
    static class AccountSimpleInfoResult {
        private String id;
        private String name;
        private String profileImgPath;

        public static AccountSimpleInfoResult from(Account account) {
            AccountSimpleInfoResult accountFollowInfo = new AccountSimpleInfoResult();
            accountFollowInfo.setId(account.getIdentification());
            accountFollowInfo.setName(account.getName());

            if (account.getProfileImage() != null) {
                accountFollowInfo.setProfileImgPath(account.getProfileImage().getStoredFilePath());
            }

            return accountFollowInfo;
        }
    }
}
