package com.test.teamlog.domain.post.dto;

import com.test.teamlog.domain.account.dto.UserRequest;
import com.test.teamlog.domain.postmedia.dto.PostMediaResult;
import com.test.teamlog.global.entity.AccessModifier;
import com.test.teamlog.domain.post.entity.Post;
import com.test.teamlog.payload.ProjectDTO;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PostResult {
    private Long id;
    private Boolean isILikeIt;
    private ProjectDTO.ProjectSimpleInfo project;
    private UserRequest.UserSimpleInfo writer;
    private AccessModifier accessModifier;
    private AccessModifier commentModifier;
    private String contents;
    private Double latitude;
    private Double longitude;
    private String address;
    private int likeCount;
    private int commentCount;
    private LocalDateTime writeTime;
    private String writeTimeStr;
    private List<String> hashtags;
    private List<PostMediaResult> media;
    private List<PostMediaResult> files;

    public static PostResult of(Post post) {
        PostResult result = new PostResult();
        result.setId(post.getId());
        result.setProject(new ProjectDTO.ProjectSimpleInfo(post.getProject()));
        result.setWriter(new UserRequest.UserSimpleInfo(post.getWriter()));
        result.setAccessModifier(post.getAccessModifier());
        result.setCommentModifier(post.getCommentModifier());
        result.setContents(post.getContents());
        result.setAddress(post.getAddress());
        result.setLikeCount(post.getPostLikes().size());
        result.setCommentCount(post.getComments().size());
        result.setWriteTime(post.getCreateTime());
        result.setWriteTimeStr(post.getCreateTime().toString());

        if (post.getLocation() != null) {
            result.setLatitude(post.getLocation().getX());
            result.setLongitude(post.getLocation().getY());
            result.setAddress(post.getAddress());
        }

        return result;
    }

//    PostResponse.builder()
//            .isILikeIt(isILikeIt)
//                .id(post.getId())
//            .project(new ProjectDTO.ProjectSimpleInfo(post.getProject()))
//            .writer(writer)
//                .accessModifier(post.getAccessModifier())
//            .commentModifier(post.getCommentModifier())
//            .contents(post.getContents())
//            .hashtags(hashtagNameList)
//                .media(media)
//                .files(files)
//                .likeCount(likeCount)
//                .commentCount(commentCount)
//                .writeTime(post.getCreateTime())
//            .writeTimeStr(post.getCreateTime().toString())
//            .build();
//
//        if (post.getLocation() != null) {
//        postResponse.setLatitude(post.getLocation().getX());
//        postResponse.setLongitude(post.getLocation().getY());
//        postResponse.setAddress(post.getAddress());
//    }
}
