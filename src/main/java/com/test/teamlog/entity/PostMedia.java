package com.test.teamlog.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "post_media")
public class PostMedia {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name="file_name",nullable = false)
    private String fileName;

    @Column(name="stored_file_name",nullable = false)
    private String storedFileName;

    @Column(name="content_type",nullable = false)
    private String contentType;

    @ManyToOne
    @JoinColumn(name = "post_id",nullable = false)
    private Post post;

    @Column(name="is_media", nullable = false)
    private Boolean isMedia;

    public void setPost(Post post) {
        if(this.post != null) {
            this.post.getMedia().remove(this);
        }
        this.post = post;
        post.getMedia().add(this);
    }
}
