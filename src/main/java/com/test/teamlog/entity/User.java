package com.test.teamlog.entity;

import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Builder
@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
    @Id
    private String id;

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
    private List<TeamFollower> teamFollowers = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy="user", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<PostLiker> postLikers = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy="user", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<TaskPerformer> taskPerformers = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return id;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}