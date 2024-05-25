package com.app.teamlog.domain.account.dto;

import com.app.teamlog.domain.account.model.Account;
import com.app.teamlog.domain.file.info.entity.FileInfo;
import lombok.Data;

@Data
public class AccountReadDetailResult {
    private Boolean isMe;
    private Boolean isFollow;
    private String identification;
    private String name;
    private String introduction;
    private String profileImgPath;

    public static AccountReadDetailResult from(Account account) {
        AccountReadDetailResult result = new AccountReadDetailResult();
        result.setIdentification(account.getIdentification());
        result.setName(account.getName());
        result.setIntroduction(account.getIntroduction());

        final FileInfo profileImage = account.getProfileImage();
        if (profileImage != null) result.setProfileImgPath(profileImage.getStoredFilePath());

        return result;
    }
}
