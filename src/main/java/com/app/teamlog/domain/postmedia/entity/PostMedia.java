package com.app.teamlog.domain.postmedia.entity;

import com.app.teamlog.domain.file.info.entity.FileInfo;
import com.app.teamlog.domain.post.entity.Post;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "post_media")
public class PostMedia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "is_media", nullable = false)
    private Boolean isMedia;

    @OneToOne
    @JoinColumn(name = "file_info_idx", nullable = false)
    private FileInfo fileInfo;

    public void setPost(Post post) {
        if (this.post != null) {
            this.post.getMedia().remove(this);
        }
        this.post = post;
        post.getMedia().add(this);
    }

    public static PostMedia create(Post post, Boolean isMedia, FileInfo fileInfo) {
        return PostMedia.builder()
                .post(post)
                .isMedia(isMedia)
                .fileInfo(fileInfo)
                .build();
    }
}
