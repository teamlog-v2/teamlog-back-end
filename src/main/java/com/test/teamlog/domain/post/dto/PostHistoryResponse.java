package com.test.teamlog.domain.post.dto;

import com.test.teamlog.domain.account.model.Account;
import com.test.teamlog.domain.postupdatehistory.entity.PostUpdateHistory;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostHistoryResponse {
    private AccountSimpleInfoResponse writer;
    private LocalDateTime writeTime;

    public static PostHistoryResponse from(PostUpdateHistory history) {
        final PostHistoryResponse response = new PostHistoryResponse();
        response.setWriter(AccountSimpleInfoResponse.from(history.getAccount()));
        response.setWriteTime(history.getCreateTime());

        return response;
    }

    @Data
    static class AccountSimpleInfoResponse {
        private String id;
        private String name;
        private String profileImgPath;

        public static AccountSimpleInfoResponse from(Account account) {
            AccountSimpleInfoResponse accountInfo = new AccountSimpleInfoResponse();
            accountInfo.setId(account.getIdentification());
            accountInfo.setName(account.getName());

            if (account.getProfileImage() != null) {
                accountInfo.setProfileImgPath(account.getProfileImage().getStoredFilePath());
            }

            return accountInfo;
        }
    }
}