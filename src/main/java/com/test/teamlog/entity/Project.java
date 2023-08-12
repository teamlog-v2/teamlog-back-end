package com.test.teamlog.entity;

import com.test.teamlog.domain.account.model.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Project extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String introduction;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "access_modifier", nullable = false, columnDefinition = "smallint")
    private AccessModifier accessModifier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_user_id", nullable = false)
    private User master;

    @ManyToOne(fetch = FetchType.LAZY)
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
        if (team == null) {
            this.team = null;
            return;
        }
        if (this.team != null) {
            this.team.getProjects().remove(this);
        }
        this.team = team;
        team.getProjects().add(this);
    }

    public void update(String name,
                       String introduction, AccessModifier accessModifier) {
        this.name = name;
        this.accessModifier = accessModifier;

        if (StringUtils.hasText(introduction)) {
            this.introduction = introduction;
        }
    }

    public void addProjectMember(ProjectMember projectMember) {
        this.projectMembers.add(projectMember);

        if (projectMember.getProject() != this) {
            projectMember.setProject(this);
        }
    }
}
