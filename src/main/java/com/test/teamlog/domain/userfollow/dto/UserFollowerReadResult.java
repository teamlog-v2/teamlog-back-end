package com.test.teamlog.domain.userfollow.dto;

import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.domain.file.info.entity.FileInfo;
import com.test.teamlog.domain.userfollow.entity.UserFollow;
import lombok.Data;

@Data
public class UserFollowerReadResult {
    private String identification;
    private String name;
    private String profileImgPath;
    private Boolean isFollow;

    public static UserFollowerReadResult from(UserFollow userFollow) {
        final User fromUser = userFollow.getFromUser();

        UserFollowerReadResult result = new UserFollowerReadResult();
        result.setIdentification(fromUser.getIdentification());
        result.setName(fromUser.getName());

        final FileInfo profileImage = fromUser.getProfileImage();
        if (profileImage != null) result.setProfileImgPath(profileImage.getStoredFilePath());

        return result;

    }
}
