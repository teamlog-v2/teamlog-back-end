package com.test.teamlog.domain.postlike.dto;

import com.test.teamlog.domain.account.model.Account;
import com.test.teamlog.domain.file.info.entity.FileInfo;
import com.test.teamlog.domain.postlike.entity.PostLike;
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
