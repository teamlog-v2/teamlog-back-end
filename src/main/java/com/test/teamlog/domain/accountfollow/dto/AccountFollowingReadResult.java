package com.test.teamlog.domain.accountfollow.dto;

import com.test.teamlog.domain.account.model.Account;
import com.test.teamlog.domain.file.info.entity.FileInfo;
import com.test.teamlog.domain.accountfollow.entity.AccountFollow;
import lombok.Data;

@Data
public class AccountFollowingReadResult {
    private String identification;
    private String name;
    private String profileImgPath;
    private Boolean isFollow;

    public static AccountFollowingReadResult from(AccountFollow accountFollow) {
        final Account toAccount = accountFollow.getToAccount();

        AccountFollowingReadResult result = new AccountFollowingReadResult();
        result.setIdentification(toAccount.getIdentification());
        result.setName(toAccount.getName());

        final FileInfo profileImage = toAccount.getProfileImage();
        if (profileImage != null) result.setProfileImgPath(profileImage.getStoredFilePath());

        return result;

    }
}
