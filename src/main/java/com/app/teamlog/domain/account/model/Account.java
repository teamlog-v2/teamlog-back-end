package com.app.teamlog.domain.account.model;

import com.app.teamlog.domain.accountfollow.entity.AccountFollow;
import com.app.teamlog.domain.file.info.entity.FileInfo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
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
    private List<AccountFollow> followers = new ArrayList<>(); // 나를 팔로우 하는 사람들

    @Builder.Default
    @OneToMany(mappedBy= "fromAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<AccountFollow> followings = new ArrayList<>(); // 내가 팔로우 하는 사람들

    public void update(String name, String introduction, FileInfo profileImage) {
        this.name = name;
        this.introduction = introduction;
        this.profileImage = profileImage;
    }

    public void updateProfileImage(FileInfo profileImage) {
        this.profileImage = profileImage;
    }
}