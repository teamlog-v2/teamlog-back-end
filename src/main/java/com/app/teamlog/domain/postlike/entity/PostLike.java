package com.app.teamlog.domain.postlike.entity;

import com.app.teamlog.domain.account.model.Account;
import com.app.teamlog.domain.post.entity.Post;
import jakarta.persistence.*;
import lombok.*;



@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "post_like",
        uniqueConstraints={
                @UniqueConstraint(
                        columnNames={"account_id","post_id"}
                )
        }
)
public class PostLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    public void setPost(Post post) {
        if(this.post != null) {
            this.post.getPostLikes().remove(this);
        }
        this.post = post;
        post.getPostLikes().add(this);
    }

    public void setAccount(Account account) {
        this.account = account;
        post.getPostLikes().add(this);
    }
}
