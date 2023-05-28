package com.test.teamlog.entity;

import com.test.teamlog.domain.account.model.User;

import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "post_liker",
        uniqueConstraints={
                @UniqueConstraint(
                        columnNames={"user_id","post_id"}
                )
        }
)
public class PostLiker {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    public void setPost(Post post) {
        if(this.post != null) {
            this.post.getPostLikers().remove(this);
        }
        this.post = post;
        post.getPostLikers().add(this);
    }

    public void setUser(User user) {
        if(this.user != null) {
            this.user.getPostLikers().remove(this);
        }
        this.user = user;
        post.getPostLikers().add(this);
    }
}
