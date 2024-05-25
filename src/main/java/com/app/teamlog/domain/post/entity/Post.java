package com.app.teamlog.domain.post.entity;

import com.app.teamlog.domain.account.model.Account;
import com.app.teamlog.domain.comment.entity.Comment;
import com.app.teamlog.domain.postlike.entity.PostLike;
import com.app.teamlog.domain.postmedia.entity.PostMedia;
import com.app.teamlog.domain.posttag.entity.PostTag;
import com.app.teamlog.domain.postupdatehistory.entity.PostUpdateHistory;
import com.app.teamlog.domain.project.entity.Project;
import com.app.teamlog.global.entity.AccessModifier;
import com.app.teamlog.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Post extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000, nullable = false)
    private String contents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_account_id", nullable = false)
    private Account writer;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "access_modifier", nullable = false, columnDefinition = "smallint")
    private AccessModifier accessModifier;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "comment_modifier", nullable = false, columnDefinition = "smallint")
    private AccessModifier commentModifier;

    private Point location;

    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<PostMedia> media = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<PostTag> hashtagList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<Comment> comments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<PostLike> postLikes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<PostUpdateHistory> postUpdateHistoryList = new ArrayList<>();

    public void addHashTags(List<PostTag> tags) {
        if (CollectionUtils.isEmpty(tags)) return;
        this.hashtagList.addAll(tags);
    }

    public void removeHashTags(List<PostTag> tags) {
        if (CollectionUtils.isEmpty(tags)) return;
        this.hashtagList.removeAll(tags);
    }

    public void addPostUpdateHistory(PostUpdateHistory postUpdateHistory) {
        if (postUpdateHistory == null) return;

        this.postUpdateHistoryList.add(postUpdateHistory);
    }

    public void addAllPostMedia(List<PostMedia> postMediaList) {
        if (CollectionUtils.isEmpty(postMediaList)) return;

        this.media.addAll(postMediaList);
    }

    public void update(String contents,
                       AccessModifier accessModifier,
                       AccessModifier commentModifier,
                       Point location,
                       String address) {
        this.contents = contents;
        this.accessModifier = accessModifier;
        this.commentModifier = commentModifier;
        this.location = location;
        this.address = address;
    }

    public void setProject(Project project) {
        if (this.project != null) {
            this.project.getPosts().remove(this);
        }
        this.project = project;
        project.getPosts().add(this);
    }
}
