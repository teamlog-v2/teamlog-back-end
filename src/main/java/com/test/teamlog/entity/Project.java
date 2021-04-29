package com.test.teamlog.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project extends BaseTimeEntity{
    @Id @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    private String introduction;

    @Enumerated(EnumType.STRING)
    @Column(name = "access_modifier",nullable = false)
    private AccessModifier accessModifier;

    @ManyToOne
    @JoinColumn(name = "master_user_id", nullable = false)
    private User master;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectMember> projectMembers = new ArrayList<ProjectMember>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectFollower> projectFollowers = new ArrayList<ProjectFollower>();
}
