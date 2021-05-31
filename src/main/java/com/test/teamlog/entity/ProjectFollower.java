package com.test.teamlog.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "project_follower",
        uniqueConstraints={
                @UniqueConstraint(
                        columnNames={"user_id","project_id"}
                )
        }
)
public class ProjectFollower {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "project_id",nullable = false)
    private Project project;

    public void setProject(Project project) {
        if(this.project != null) {
            this.project.getProjectFollowers().remove(this);
        }
        this.project = project;
        project.getProjectFollowers().add(this);
    }
}
