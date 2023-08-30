package com.test.teamlog.domain.postlike.dto;

import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.domain.postlike.entity.PostLike;
import lombok.Data;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Data
public class PostLikerResult {
    private String id;
    private String name;
    private String profileImgPath;

    public static PostLikerResult from(PostLike postLike) {
        PostLikerResult result = new PostLikerResult();
        final User user = postLike.getUser();
        result.setId(user.getIdentification());
        result.setName(user.getName());
        String imgPath = user.getProfileImgPath() != null ?
                ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/resources/")
                        .path(user.getProfileImgPath())
                        .toUriString() :
                null;
        result.setProfileImgPath(imgPath);

        return result;
    }
}
