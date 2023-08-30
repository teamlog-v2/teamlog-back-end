package com.test.teamlog.domain.post.dto;

import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.global.entity.AccessModifier;
import com.test.teamlog.domain.post.entity.Post;
import com.test.teamlog.domain.project.entity.Project;
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


    public Post toPost(Project project, User currentUser) {
        return Post.builder()
                .contents(this.contents)
                .accessModifier(this.accessModifier)
                .commentModifier(this.commentModifier)
                .address(this.address)
                .location(this.location)
                .writer(currentUser)
                .project(project)
                .build();
    }
}
