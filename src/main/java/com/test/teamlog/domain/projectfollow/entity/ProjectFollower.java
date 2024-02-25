package com.test.teamlog.domain.projectfollow.entity;

import com.test.teamlog.domain.account.model.Account;
import com.test.teamlog.domain.project.entity.Project;
import jakarta.persistence.*;
import lombok.*;



@Entity
@Builder
@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "project_follower",
        uniqueConstraints={
                @UniqueConstraint(
                        columnNames={"account_id","project_id"}
                )
        }
)
public class ProjectFollower {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id",nullable = false)
    private Account account;

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

    public static ProjectFollower create(Project project, Account account) {
        ProjectFollower projectFollower = ProjectFollower.builder()
                .project(project)
                .account(account)
                .build();

        project.getProjectFollowers().add(projectFollower);

        return projectFollower;
    }
}
