package com.test.teamlog.entity;

import com.test.teamlog.domain.account.model.User;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "project_join",
        uniqueConstraints = @UniqueConstraint(columnNames = {"project_id", "user_id"}))
public class ProjectJoin extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "is_accepted", nullable = false)
    private Boolean isAccepted; // 수락

    @Column(name = "is_applied", nullable = false)
    private Boolean isApplied; // 가입 신청

    @Column(name = "is_invited", nullable = false)
    private Boolean isInvited; // 초대

    public void setProject(Project project) {
        if (this.project != null) {
            this.project.getProjectJoins().remove(this);
        }
        this.project = project;
        project.getProjectJoins().add(this);
    }

    public static ProjectJoin createInvitation(Project project, User user) {
        return ProjectJoin.builder()
                .project(project)
                .user(user)
                .isAccepted(false)
                .isApplied(false)
                .isInvited(true)
                .build();
    }

    public static ProjectJoin createApplication(Project project, User user) {
        return ProjectJoin.builder()
                .project(project)
                .user(user)
                .isAccepted(false)
                .isApplied(true)
                .isInvited(false)
                .build();
    }

    public void update(Boolean isInvited, Boolean isApplied) {
        this.isInvited = isInvited;
        this.isApplied = isApplied;
        setUpdateTimeToNow();
    }
}
