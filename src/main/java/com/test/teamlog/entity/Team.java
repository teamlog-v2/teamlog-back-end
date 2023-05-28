package com.test.teamlog.entity;

import lombok.*;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
public class Team extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String introduction;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "access_modifier",nullable = false, columnDefinition = "smallint")
    private AccessModifier accessModifier;

    @ManyToOne
    @JoinColumn(name = "master_user_id", nullable = false)
    private User master;

    @Builder.Default
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<TeamMember> teamMembers = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<TeamFollower> teamFollowers = new ArrayList<TeamFollower>();

    @Builder.Default
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<TeamJoin> teamJoins = new ArrayList<TeamJoin>();

    @Builder.Default
    @OneToMany(mappedBy = "team")
    @BatchSize(size = 10)
    private List<Project> projects = new ArrayList<Project>();
}
