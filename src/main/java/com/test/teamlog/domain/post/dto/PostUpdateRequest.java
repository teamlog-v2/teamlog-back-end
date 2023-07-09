package com.test.teamlog.domain.post.dto;

import com.test.teamlog.entity.AccessModifier;
import lombok.Data;

import java.util.List;

@Data
public class PostUpdateRequest {
    private String contents;
    private AccessModifier accessModifier;
    private AccessModifier commentModifier;
    private Double latitude;
    private Double longitude;
    private String address;
    private Long projectId;
    private List<String> hashtags;
    private List<Long> deletedFileIdList;

    public PostUpdateInput toInput() {
        PostUpdateInput input = new PostUpdateInput();
        input.setContents(this.contents);
        input.setAccessModifier(this.accessModifier);
        input.setCommentModifier(this.commentModifier);
        input.setLatitude(this.latitude);
        input.setLongitude(this.longitude);
        input.setAddress(this.address);
        input.setProjectId(this.projectId);
        input.setHashtags(this.hashtags);
        input.setDeletedFileIdList(this.deletedFileIdList);

        return input;
    }
}