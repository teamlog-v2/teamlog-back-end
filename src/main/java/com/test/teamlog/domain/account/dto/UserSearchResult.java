package com.test.teamlog.domain.account.dto;

import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.domain.file.info.entity.FileInfo;
import lombok.Data;

@Data
public class UserSearchResult {
    private String id;
    private String name;
    private String profileImgPath;

    public static UserSearchResult from(User user) {
        UserSearchResult result = new UserSearchResult();
        result.setId(user.getIdentification());
        result.setName(user.getName());

        final FileInfo profileImage = user.getProfileImage();
        if (profileImage != null) result.setProfileImgPath(profileImage.getStoredFilePath());

        return result;
    }
}
