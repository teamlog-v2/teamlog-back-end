package com.test.teamlog.entity;

import com.test.teamlog.domain.account.model.User;

import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comment_mention")
public class CommentMention {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
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
