package com.app.teamlog.domain.posttag.entity;

import com.app.teamlog.domain.post.entity.Post;
import jakarta.persistence.*;
import lombok.*;



@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "post_tag")
public class PostTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    public void setPost(Post post) {
        // 기존의 post와 post_tag의 관계를 제거
        // 제거해주지 않으면 추후 post의 post_tag를 조회할 때 이전에 설정했던 post_tag가 조회될 수 있다.
        if (this.post != null) {
            this.post.getHashtagList().remove(this);
        }

        this.post = post;
        post.getHashtagList().add(this);
    }
}
