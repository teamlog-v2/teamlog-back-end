package com.test.teamlog.entity;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment extends BaseTimeEntity {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;

    @Builder.Default
    @OneToMany(mappedBy = "parentComment", orphanRemoval = true)
    private List<Comment> childComments = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false)
    private String contents;

    @ManyToOne
    @JoinColumn(name = "writer_user_id", nullable = false)
    private User writer;

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

    public void addCommentMentions(List<CommentMention> mentions) {
        this.commentMentions.addAll(mentions);
    }

    public void removeCommentMentions(List<CommentMention> mentions) {
        this.commentMentions.removeAll(mentions);
    }

}
