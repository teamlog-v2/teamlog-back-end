package com.test.teamlog.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "post_media")
public class PostMedia {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name="file_name",nullable = false)
    private String fileName;

    @Column(name="stored_file_name",nullable = false)
    private String storedFileName;

    @Column(name="file_download_uri",nullable=false)
    private String fileDownloadUri;

    private Long size;

    @Column(name="content_type",nullable = false)
    private String contentType;

    @ManyToOne
    @JoinColumn(name = "post_id",nullable = false)
    private Post post;

    public void setPost(Post post) {
        if(this.post != null) {
            this.post.getMedia().remove(this);
        }
        this.post = post;
        post.getMedia().add(this);
    }
}
