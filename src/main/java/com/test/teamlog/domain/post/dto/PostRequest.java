package com.test.teamlog.domain.post.dto;

import com.test.teamlog.entity.AccessModifier;
import lombok.Data;

import java.util.List;

@Data
public class PostRequest {
    private String contents;
    private AccessModifier accessModifier;
    private AccessModifier commentModifier;
    private Double latitude;
    private Double longitude;
    private String address;
    private Long projectId;
    private List<String> hashtags;

    public PostCreateInput toInput() {
        PostCreateInput input = new PostCreateInput();
        input.setContents(this.contents);
        input.setAccessModifier(this.accessModifier);
        input.setCommentModifier(this.commentModifier);
        input.setLatitude(this.latitude);
        input.setLongitude(this.longitude);
        input.setAddress(this.address);
        input.setProjectId(this.projectId);
        input.setHashtags(this.hashtags);

        return input;
    }
}
