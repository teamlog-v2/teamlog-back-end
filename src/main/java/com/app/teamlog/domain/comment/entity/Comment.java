package com.app.teamlog.domain.comment.entity;

import com.app.teamlog.domain.account.model.Account;
import com.app.teamlog.domain.post.entity.Post;
import com.app.teamlog.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;

    @Builder.Default
    @OneToMany(mappedBy = "parentComment", orphanRemoval = true)
    private List<Comment> childComments = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false)
    private String contents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_account_id", nullable = false)
    private Account writer;

    @Builder.Default
    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentMention> commentMentions = new ArrayList<>();

    public void setPost(Post post) {
        if (this.post != null) {
            this.post.getComments().remove(this);
        }
        this.post = post;
        post.getComments().add(this);
    }

    public void update(String contents) {
        this.contents = contents;
    }

    public void addCommentMentions(List<CommentMention> mentions) {
        this.commentMentions.addAll(mentions);
    }

    public void removeCommentMentions(List<CommentMention> mentions) {
        this.commentMentions.removeAll(mentions);
    }

}
