package com.app.teamlog.domain.post.dto;

import com.app.teamlog.global.entity.AccessModifier;
import lombok.Data;

import java.util.List;

@Data
public class PostUpdateInput {
    private String contents;
    private AccessModifier accessModifier;
    private AccessModifier commentModifier;
    private Double latitude;
    private Double longitude;
    private String address;
    private Long projectId;
    private List<String> hashtags;
    private List<Long> deletedFileIdList;
}