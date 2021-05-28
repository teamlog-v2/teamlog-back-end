package com.test.teamlog.entity;

import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
public class Project extends BaseTimeEntity{
    @Id @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    private String introduction;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "access_modifier",nullable = false, columnDefinition = "TINYINT(1)")
    private AccessModifier accessModifier;

    @ManyToOne
    @JoinColumn(name = "master_user_id", nullable = false)
    private User master;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    private String thumbnail;

    @Builder.Default
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<Post> posts = new ArrayList<Post>();

    @Builder.Default
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<ProjectMember> projectMembers = new ArrayList<ProjectMember>();

    @Builder.Default
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<ProjectFollower> projectFollowers = new ArrayList<ProjectFollower>();

    @Builder.Default
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<ProjectJoin> projectJoins = new ArrayList<ProjectJoin>();

    @Builder.Default
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<Task> tasks = new ArrayList<Task>();

    public void setTeam(Team team) {
        if(this.team != null) {
            this.team.getProjects().remove(this);
        }
        this.team = team;
        team.getProjects().add(this);
    }
}
