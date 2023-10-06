package com.test.teamlog.domain.account.model;

import com.test.teamlog.domain.postlike.entity.PostLike;
import com.test.teamlog.domain.projectfollow.entity.ProjectFollower;
import com.test.teamlog.domain.task.entity.TaskPerformer;
import com.test.teamlog.domain.userfollow.entity.UserFollow;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

// FIXME: 추후 class 이름 Account로 변경
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
    private List<UserFollow> followers = new ArrayList<>(); // 나를 팔로우 하는 사람들

    @Builder.Default
    @OneToMany(mappedBy="fromUser", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<UserFollow> followings = new ArrayList<>(); // 내가 팔로우 하는 사람들

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