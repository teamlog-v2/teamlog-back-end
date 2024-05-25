package com.app.teamlog.domain.postlike.dto;

import com.app.teamlog.domain.account.model.Account;
import com.app.teamlog.domain.file.info.entity.FileInfo;
import com.app.teamlog.domain.postlike.entity.PostLike;
import lombok.Data;

@Data
public class PostLikerResult {
    private String id;
    private String name;
    private String profileImgPath;

    public static PostLikerResult from(PostLike postLike) {
        PostLikerResult result = new PostLikerResult();
        final Account account = postLike.getAccount();
        result.setId(account.getIdentification());
        result.setName(account.getName());

        final FileInfo profileImage = account.getProfileImage();
        if (profileImage != null) result.setProfileImgPath(profileImage.getStoredFilePath());

        return result;
    }
}
