package com.test.teamlog.domain.account.model;

import com.test.teamlog.entity.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "account")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;
    @Column(nullable = false, unique = true)
    private String identification;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    private String introduction;

    @Column(name = "profile_img_path")
    private String profileImgPath;

    @Builder.Default
    @OneToMany(mappedBy="toUser", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<UserFollow> followers = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy="fromUser", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<UserFollow> following = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy="user", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<ProjectFollower> projectFollowers = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy="user", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<PostLike> postLikes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy="user", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<TaskPerformer> taskPerformers = new ArrayList<>();
}