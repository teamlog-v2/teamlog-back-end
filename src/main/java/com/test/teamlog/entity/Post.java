package com.test.teamlog.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.geo.Point;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Getter @Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Post {
    @Id @GeneratedValue
    private Long id;

    @Column(length = 1000, nullable = false)
    private String contents;

    @ManyToOne
    @JoinColumn(name = "writer_user_id", nullable = false)
    private User writer;

    @Enumerated(EnumType.STRING)
    @Column(name = "access_modifier", nullable = false)
    private AccessModifier accessModifier;

    @Enumerated(EnumType.STRING)
    @Column(name = "comment_modifier", nullable = false)
    private AccessModifier commentModifier;

    private Point location;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
}
