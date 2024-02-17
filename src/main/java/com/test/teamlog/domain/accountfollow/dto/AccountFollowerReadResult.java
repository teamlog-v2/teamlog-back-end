package com.test.teamlog.domain.accountfollow.dto;

import com.test.teamlog.domain.account.model.Account;
import com.test.teamlog.domain.file.info.entity.FileInfo;
import com.test.teamlog.domain.accountfollow.entity.AccountFollow;
import lombok.Data;

@Data
public class AccountFollowerReadResult {
    private String identification;
    private String name;
    private String profileImgPath;
    private Boolean isFollow;

    public static AccountFollowerReadResult from(AccountFollow accountFollow) {
        final Account fromAccount = accountFollow.getFromAccount();

        AccountFollowerReadResult result = new AccountFollowerReadResult();
        result.setIdentification(fromAccount.getIdentification());
        result.setName(fromAccount.getName());

        final FileInfo profileImage = fromAccount.getProfileImage();
        if (profileImage != null) result.setProfileImgPath(profileImage.getStoredFilePath());

        return result;

    }
}
