package com.test.teamlog.domain.account.dto;

import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.domain.file.info.entity.FileInfo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserValidateResult {
    private String id;
    private String name;
    private String profileImgPath;

    public static UserValidateResult from(User user) {
        UserValidateResult result = new UserValidateResult();
        result.setId(user.getIdentification());
        result.setName(user.getName());

        final FileInfo profileImage = user.getProfileImage();
        if (profileImage != null) result.setProfileImgPath(profileImage.getStoredFilePath());

        return result;
    }
}
