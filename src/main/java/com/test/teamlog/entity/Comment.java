package com.test.teamlog.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment extends BaseTimeEntity {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;

    @OneToMany(mappedBy = "parentComment",orphanRemoval = true)
    private List<Comment> childComments = new ArrayList<Comment>();

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false)
    private String contents;

    @ManyToOne
    @JoinColumn(name = "writer_user_id", nullable = false)
    private User writer;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentMention> commentMentions = new ArrayList<CommentMention>();

    public void setPost(Post post) {
        if(this.post != null) {
            this.post.getComments().remove(this);
        }
        this.post = post;
        post.getComments().add(this);
    }
}
