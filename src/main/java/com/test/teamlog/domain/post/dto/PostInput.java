package com.test.teamlog.domain.post.dto;

import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.entity.AccessModifier;
import com.test.teamlog.entity.Post;
import com.test.teamlog.entity.Project;
import lombok.Data;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import java.util.List;

@Data
public class PostInput {
    private String contents;
    private AccessModifier accessModifier;
    private AccessModifier commentModifier;
    private Double latitude;
    private Double longitude;
    private String address;
    private Long projectId;
    private List<String> hashtags;


    public Post toPost(Project project, User currentUser) {
        return Post.builder()
                .contents(contents)
                .accessModifier(accessModifier)
                .commentModifier(commentModifier)
                .address(address)
                .location(makeLocation())
                .writer(currentUser)
                .project(project)
                .build();
    }

    private Point makeLocation() {
        Point point = null;
        if (latitude != null && longitude != null) {
            GeometryFactory geometryFactory = new GeometryFactory();
            point = geometryFactory.createPoint(new Coordinate(latitude, longitude));
        }

        return point;
    }
}
