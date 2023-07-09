package com.test.teamlog.domain.postmedia.dto;

import com.test.teamlog.entity.PostMedia;
import lombok.Data;

@Data
public class PostMediaResult {
    private Long id;
    private String contentType;
    private String fileName;
    private String fileDownloadUri;

    public static PostMediaResult from(PostMedia postMedia) {
        PostMediaResult result = new PostMediaResult();
        result.setId(postMedia.getId());
        result.setContentType(postMedia.getContentType());
        result.setFileName(postMedia.getFileName());
        return result;
    }
}
