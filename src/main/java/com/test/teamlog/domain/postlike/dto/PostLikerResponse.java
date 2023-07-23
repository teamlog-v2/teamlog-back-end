package com.test.teamlog.domain.postlike.dto;

import lombok.Data;

@Data
public class PostLikerResponse {
    private String id;
    private String name;
    private String profileImgPath;

    public static PostLikerResponse from(PostLikerResult result) {
        PostLikerResponse response = new PostLikerResponse();
        response.setId(result.getId());
        response.setName(result.getName());
        response.setProfileImgPath(result.getProfileImgPath());

        return response;
    }
}
