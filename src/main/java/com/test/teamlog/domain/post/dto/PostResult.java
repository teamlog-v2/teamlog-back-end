package com.test.teamlog.domain.post.dto;

import com.test.teamlog.domain.account.model.Account;
import com.test.teamlog.domain.post.entity.Post;
import com.test.teamlog.domain.postmedia.dto.PostMediaResult;
import com.test.teamlog.domain.project.entity.Project;
import com.test.teamlog.global.entity.AccessModifier;
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
    private ProjectSimpleInfoResult project;
    private AccountSimpleInfoResult writer;
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
        result.setProject(ProjectSimpleInfoResult.from(post.getProject()));
        result.setWriter(AccountSimpleInfoResult.from(post.getWriter()));
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

    @Data
    static class AccountSimpleInfoResult {
        private String id;
        private String name;
        private String profileImgPath;

        static AccountSimpleInfoResult from(Account account) {
            AccountSimpleInfoResult result = new AccountSimpleInfoResult();
            result.setId(account.getIdentification());
            result.setName(account.getName());

            if (account.getProfileImage() != null) {
                result.setProfileImgPath(account.getProfileImage().getStoredFilePath());
            }

            return result;
        }
    }

    @Data
    static class ProjectSimpleInfoResult {
        private Long id;
        private String name;

        static ProjectSimpleInfoResult from(Project project) {
            ProjectSimpleInfoResult projectSimpleInfoResult = new ProjectSimpleInfoResult();
            projectSimpleInfoResult.setId(project.getId());
            projectSimpleInfoResult.setName(project.getName());

            return projectSimpleInfoResult;
        }
    }
}
