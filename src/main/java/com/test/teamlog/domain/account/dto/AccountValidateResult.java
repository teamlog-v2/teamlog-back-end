package com.test.teamlog.domain.account.dto;

import com.test.teamlog.domain.account.model.Account;
import com.test.teamlog.domain.file.info.entity.FileInfo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountValidateResult {
    private String id;
    private String name;
    private String profileImgPath;

    public static AccountValidateResult from(Account account) {
        AccountValidateResult result = new AccountValidateResult();
        result.setId(account.getIdentification());
        result.setName(account.getName());

        final FileInfo profileImage = account.getProfileImage();
        if (profileImage != null) result.setProfileImgPath(profileImage.getStoredFilePath());

        return result;
    }
}
