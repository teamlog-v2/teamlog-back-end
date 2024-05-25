package com.app.teamlog.domain.post.dto;

import com.app.teamlog.domain.account.model.Account;
import com.app.teamlog.global.entity.AccessModifier;
import com.app.teamlog.domain.post.entity.Post;
import com.app.teamlog.domain.project.entity.Project;
import lombok.Data;
import org.locationtech.jts.geom.Point;

import java.util.List;

@Data
public class PostCreateInput {
    private String contents;
    private AccessModifier accessModifier;
    private AccessModifier commentModifier;
    private Double latitude;
    private Double longitude;
    private Point location;
    private String address;
    private Long projectId;
    private List<String> hashtags;


    public Post toPost(Project project, Account currentAccount) {
        return Post.builder()
                .contents(this.contents)
                .accessModifier(this.accessModifier)
                .commentModifier(this.commentModifier)
                .address(this.address)
                .location(this.location)
                .writer(currentAccount)
                .project(project)
                .build();
    }
}
