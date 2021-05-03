package com.test.teamlog.entity;

import lombok.*;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "post_tag")
public class PostTag {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "post_id",nullable = false)
    private Post post;

    public void setPost(Post post) {
        if(this.post != null) {
            this.post.getHashtags().remove(this);
        }
        this.post = post;
        post.getHashtags().add(this);
    }
}
