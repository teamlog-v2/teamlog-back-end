package com.test.teamlog.domain.post.dto;

import com.test.teamlog.entity.AccessModifier;
import lombok.Data;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

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

    private Point makeLocation() {
        Point point = null;
        if (latitude != null && longitude != null) {
            GeometryFactory geometryFactory = new GeometryFactory();
            point = geometryFactory.createPoint(new Coordinate(latitude, longitude));
        }

        return point;
    }
}