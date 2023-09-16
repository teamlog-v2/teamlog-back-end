package com.test.teamlog.domain.projectinvitation.entity;


import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.domain.project.entity.Project;
import com.test.teamlog.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ProjectInvitation extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @ManyToOne
    @JoinColumn(name = "project_idx", nullable = false)
    private Project project;

    @ManyToOne
    @JoinColumn(name = "invitor_idx", nullable = false)
    private User inviter;

    @ManyToOne
    @JoinColumn(name = "invitee_idx", nullable = false)
    private User invitee;

    @Column(name = "is_invited", nullable = false)
    private boolean isAccepted;

    public void update() {
        setUpdateTimeToNow();
    }
}
