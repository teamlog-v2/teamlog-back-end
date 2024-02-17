package com.test.teamlog.domain.projectmember.entity;

import com.test.teamlog.domain.account.model.Account;
import com.test.teamlog.domain.project.entity.Project;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Builder
@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "project_member",
        uniqueConstraints = @UniqueConstraint(columnNames = {"project_id", "account_id"}))
public class ProjectMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id",nullable = false)
    private Project project;

    @ManyToOne
    @JoinColumn(name = "account_id",nullable = false)
    private Account account;

    @CreatedDate
    @Column(name = "join_time",nullable = false)
    private LocalDateTime joinTime;

    public void setProject(Project project) {
        if(this.project != null) {
            this.project.getProjectMembers().remove(this);
        }
        this.project = project;
        project.getProjectMembers().add(this);
    }

    public static ProjectMember create(Project project, Account account) {
        final ProjectMember projectMember = ProjectMember.builder()
                .project(project)
                .account(account)
                .build();

        project.getProjectMembers().add(projectMember);
        return projectMember;
    }
}
