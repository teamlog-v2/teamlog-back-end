package com.test.teamlog.domain.comment.dto;

import com.test.teamlog.domain.account.model.Account;
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
    private AccountSimpleInfoResult writer;
    private List<String> commentMentions;
    private LocalDateTime writeTime;

    public static CommentCreateResult of(Comment comment) {
        CommentCreateResult result = new CommentCreateResult();
        result.setIsMyComment(true);
        result.setId(comment.getId());
        result.setContents(comment.getContents());
        result.setChildCommentCount(comment.getChildComments().size());
        result.setWriter(AccountSimpleInfoResult.from(comment.getWriter()));
        result.setCommentMentions(comment.getCommentMentions().stream().map(c -> c.getTargetAccount().getIdentification()).collect(Collectors.toList()));
        result.setWriteTime(comment.getCreateTime());

        return result;
    }

    @Data
    static class AccountSimpleInfoResult {
        private String id;
        private String name;
        private String profileImgPath;

        public static AccountSimpleInfoResult from(Account account) {
            AccountSimpleInfoResult accountInfo = new AccountSimpleInfoResult();
            accountInfo.setId(account.getIdentification());
            accountInfo.setName(account.getName());

            if (account.getProfileImage() != null) {
                accountInfo.setProfileImgPath(account.getProfileImage().getStoredFilePath());
            }

            return accountInfo;
        }
    }
}
