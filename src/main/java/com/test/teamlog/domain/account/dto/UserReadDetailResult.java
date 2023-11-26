package com.test.teamlog.domain.account.dto;

import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.domain.file.info.entity.FileInfo;
import lombok.Data;

@Data
public class UserReadDetailResult {
    private Boolean isMe;
    private Boolean isFollow;
    private String identification;
    private String name;
    private String introduction;
    private String profileImgPath;

    public static UserReadDetailResult from(User user) {
        UserReadDetailResult result = new UserReadDetailResult();
        result.setIdentification(user.getIdentification());
        result.setName(user.getName());
        result.setIntroduction(user.getIntroduction());

        final FileInfo profileImage = user.getProfileImage();
        if (profileImage != null) result.setProfileImgPath(profileImage.getStoredFilePath());

        return result;
    }
}
