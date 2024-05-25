package com.app.teamlog.domain.account.dto;

import com.app.teamlog.domain.account.model.Account;
import com.app.teamlog.domain.file.info.entity.FileInfo;
import lombok.Data;

@Data
public class AccountSearchResult {
    private String id;
    private String name;
    private String profileImgPath;

    public static AccountSearchResult from(Account account) {
        AccountSearchResult result = new AccountSearchResult();
        result.setId(account.getIdentification());
        result.setName(account.getName());

        final FileInfo profileImage = account.getProfileImage();
        if (profileImage != null) result.setProfileImgPath(profileImage.getStoredFilePath());

        return result;
    }
}
