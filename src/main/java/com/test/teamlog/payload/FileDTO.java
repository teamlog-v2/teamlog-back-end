package com.test.teamlog.payload;

import com.test.teamlog.entity.AccessModifier;
import com.test.teamlog.entity.PostMedia;
import com.test.teamlog.entity.PostTag;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.core.io.Resource;
import org.springframework.data.geo.Point;

import java.util.ArrayList;
import java.util.List;

public class FileDTO {
    @Getter
    public static class UploadRequest {
        private String contents;
        private AccessModifier accessModifier;
        private AccessModifier commentModifier;
        private Point location;
        private String writerId;
        private Long projectId;
        private List<PostMedia> media = new ArrayList<PostMedia>();
        private List<PostTag> hashtags = new ArrayList<PostTag>();
    }

    @Data
    @Builder
    public static class PostMediaResponse {
        private String contents;
        private AccessModifier accessModifier;
        private AccessModifier commentModifier;
        private Point location;
        private String writerId;
        private Long projectId;
        private List<PostMedia> media = new ArrayList<PostMedia>();
        private List<PostTag> hashtags = new ArrayList<PostTag>();
    }

    @Data
    @Builder
    public static class FileResourceInfo {
        private String contentType;
        private String fileName;
        private Resource resource;
    }
}
