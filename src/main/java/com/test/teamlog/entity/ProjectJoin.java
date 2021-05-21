package com.test.teamlog.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@Setter @Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "project_join",
        uniqueConstraints =@UniqueConstraint(columnNames = {"project_id", "user_id"}))
public class ProjectJoin extends BaseTimeEntity {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id",nullable = false)
    private Project project;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @Column(name = "is_accepted",nullable = false)
    private Boolean isAccepted;

    @Column(name = "is_invited",nullable = false)
    private Boolean isInvited;

    public void setProject(Project project) {
        if(this.project != null) {
            this.project.getProjectJoins().remove(this);
        }
        this.project = project;
        project.getProjectJoins().add(this);
    }
}
