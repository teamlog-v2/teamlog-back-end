package com.test.teamlog.domain.account.dto;

import com.test.teamlog.domain.account.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Data
@NoArgsConstructor
public class UserValidateResponse {
    private String id;
    private String name;
    private String profileImgPath;

    public static UserValidateResponse of(User user) {
        UserValidateResponse response = new UserValidateResponse();
        response.setId(user.getIdentification());
        response.setName(user.getName());

        String imgUri = null;
        if (user.getProfileImgPath() != null) {
            imgUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/resources/")
                    .path(user.getProfileImgPath())
                    .toUriString();
        }
        response.setProfileImgPath(imgUri);

        return response;
    }
}
