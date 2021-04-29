package com.test.teamlog.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "post_liker")
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
}
