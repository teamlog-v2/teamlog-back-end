package com.test.teamlog.domain.userfollow.dto;

import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.entity.UserFollow;
import lombok.Data;

@Data
public class UserFollowingReadResult {
    private String identification;
    private String name;
    private String profileImgPath;
    private Boolean isFollow;

    public static UserFollowingReadResult from(UserFollow userFollow) {
        final User toUser = userFollow.getToUser();

        UserFollowingReadResult result = new UserFollowingReadResult();
        result.setIdentification(toUser.getIdentification());
        result.setName(toUser.getName());
        result.setProfileImgPath(toUser.getProfileImgPath());

        return result;

    }
}
