package com.test.teamlog.entity;

import lombok.*;
import org.springframework.data.geo.Point;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Post extends BaseTimeEntity {
    @Id
    @GeneratedValue
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

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostMedia> media = new ArrayList<PostMedia>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostTag> hashtags = new ArrayList<PostTag>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<Comment>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true) // 애는 삭제할까말까..
    private List<PostLiker> postLikers = new ArrayList<PostLiker>();
}
