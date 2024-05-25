package com.app.teamlog.domain.comment.entity;

import com.app.teamlog.domain.account.model.Account;
import jakarta.persistence.*;
import lombok.*;



@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comment_mention")
public class CommentMention {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_account_id", nullable = false)
    private Account targetAccount;

    public void setComment(Comment comment) {
        if(this.comment != null) {
            this.comment.getCommentMentions().remove(this);
        }
        this.comment = comment;
        comment.getCommentMentions().add(this);
    }
}
