package com.test.teamlog.domain.account.dto;

import com.test.teamlog.domain.account.model.User;
import lombok.Data;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Data
public class UserSearchResult {
    private String id;
    private String name;
    private String profileImgPath;

    public static UserSearchResult from(User user) {
        UserSearchResult result = new UserSearchResult();
        result.setId(user.getIdentification());
        result.setName(user.getName());
        result.setProfileImgPath(user.getProfileImgPath() != null ?
                ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/resources/")
                        .path(user.getProfileImgPath())
                        .toUriString() :
                null
        );

        return result;
    }
}
