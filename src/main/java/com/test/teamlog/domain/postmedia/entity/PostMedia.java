package com.test.teamlog.domain.postmedia.entity;

import com.test.teamlog.domain.file.info.entity.FileInfo;
import com.test.teamlog.domain.post.entity.Post;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "post_media")
public class PostMedia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "is_media", nullable = false)
    private Boolean isMedia;

    @OneToOne
    @JoinColumn(name = "file_info_idx", nullable = false)
    private FileInfo fileInfo;

    // TODO: 삭제
    @Column(name = "stored_file_name", nullable = false)
    private String storedFileName;

    // TODO: 삭제
    @Column(name = "content_type", nullable = false)
    private String contentType;

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
