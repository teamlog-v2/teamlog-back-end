package com.test.teamlog.domain.account.dto;

import com.test.teamlog.domain.account.model.User;
import lombok.Data;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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
        String imgUri = user.getProfileImgPath() != null ?
                ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/resources/")
                        .path(user.getProfileImgPath())
                        .toUriString() :
                null;
        result.setProfileImgPath(imgUri);

        return result;
    }
}
