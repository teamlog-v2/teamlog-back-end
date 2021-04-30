package com.test.teamlog.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "comment_mention")
public class CommentMention {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @ManyToOne
    @JoinColumn(name = "target_user_id", nullable = false)
    private User targetUser;

    public void setComment(Comment comment) {
        if(this.comment != null) {
            this.comment.getCommentMentions().remove(this);
        }
        this.comment = comment;
        comment.getCommentMentions().add(this);
    }
}
