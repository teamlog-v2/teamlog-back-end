package com.test.teamlog.entity;

import lombok.*;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    private String id;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    private String introduction;

    @Column(name = "profile_img_path")
    private String profileImgPath;

    @OneToMany(mappedBy="toUser")
    @BatchSize(size = 10)
    private List<UserFollow> followers;

    @OneToMany(mappedBy="fromUser")
    @BatchSize(size = 10)
    private List<UserFollow> following;
}