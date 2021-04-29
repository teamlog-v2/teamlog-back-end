package com.test.teamlog.payload;

import com.test.teamlog.entity.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.geo.Point;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public class PostDTO {
    @Getter
    public static class PostRequest {
        private String contents;
        private AccessModifier accessModifier;
        private AccessModifier commentModifier;
        private Point location;
        private String writerId;
        private Long projectId;
        private List<String> hashtags;
    }

    @Data
    @Builder
    public static class PostResponse {
        private Long id;
        private String contents;
        private List<String> hashtags;
        private int likeCount;
        private int commentCount;
        private LocalDateTime writeTime;
    }
}
