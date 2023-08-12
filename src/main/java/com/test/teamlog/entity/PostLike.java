package com.test.teamlog.entity;

import com.test.teamlog.domain.account.model.User;
import jakarta.persistence.*;
import lombok.*;



@Entity
@Builder
@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "post_like",
        uniqueConstraints={
                @UniqueConstraint(
                        columnNames={"user_id","post_id"}
                )
        }
)
public class PostLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

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

    public void setUser(User user) {
        if(this.user != null) {
            this.user.getPostLikes().remove(this);
        }
        this.user = user;
        post.getPostLikes().add(this);
    }
}
