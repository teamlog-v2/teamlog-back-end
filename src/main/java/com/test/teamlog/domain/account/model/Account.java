package com.test.teamlog.domain.account.model;

import com.test.teamlog.domain.file.info.entity.FileInfo;
import com.test.teamlog.domain.postlike.entity.PostLike;
import com.test.teamlog.domain.projectfollow.entity.ProjectFollower;
import com.test.teamlog.domain.task.entity.TaskPerformer;
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
@Table(name = "account", uniqueConstraints = @UniqueConstraint(columnNames = {"identification", "auth_type"}))
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column(nullable = false, unique = true)
    private String identification;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_type", nullable = false, columnDefinition = "text")
    private AuthType authType;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    private String introduction;

    @JoinColumn(name = "profile_image_idx")
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private FileInfo profileImage;

    @Builder.Default
    @OneToMany(mappedBy= "toAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<com.test.teamlog.domain.accountfollow.entity.AccountFollow> followers = new ArrayList<>(); // 나를 팔로우 하는 사람들

    @Builder.Default
    @OneToMany(mappedBy= "fromAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<com.test.teamlog.domain.accountfollow.entity.AccountFollow> followings = new ArrayList<>(); // 내가 팔로우 하는 사람들

    @Builder.Default
    @OneToMany(mappedBy= "account", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<ProjectFollower> projectFollowers = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy= "account", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<PostLike> postLikes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy= "account", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<TaskPerformer> taskPerformers = new ArrayList<>();

    public void updateProfileImage(FileInfo profileImage) {
        this.profileImage = profileImage;
    }
}