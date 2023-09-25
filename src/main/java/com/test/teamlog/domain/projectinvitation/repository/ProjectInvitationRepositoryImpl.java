package com.test.teamlog.domain.projectinvitation.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.domain.project.entity.Project;
import com.test.teamlog.domain.projectinvitation.dto.ProjectInvitationReadInviteeResult;
import com.test.teamlog.domain.projectinvitation.dto.ProjectInvitationReadPendingResult;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.test.teamlog.domain.projectinvitation.entity.QProjectInvitation.projectInvitation;

@RequiredArgsConstructor
public class ProjectInvitationRepositoryImpl implements ProjectInvitationCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<ProjectInvitationReadInviteeResult> findAllByProjectAndAcceptedIsFalse(Project project) {
        return jpaQueryFactory
                .select(Projections.fields(ProjectInvitationReadInviteeResult.class,
                        projectInvitation.invitee.identification.as("inviteeIdentification"),
                        projectInvitation.invitee.name.as("inviteeName"),
                        projectInvitation.inviter.identification.as("inviterIdentification"),
                        projectInvitation.inviter.name.as("inviterName")
                ))
                .from(projectInvitation)
                .where(projectInvitation.project.eq(project), projectInvitation.isAccepted.isFalse())
                .fetch();
    }

    @Override
    public List<ProjectInvitationReadPendingResult> findAllByUserAndAcceptedIsFalse(User user) {
        return jpaQueryFactory
                .select(Projections.fields(ProjectInvitationReadPendingResult.class,
                        projectInvitation.idx.as("idx"),
                        projectInvitation.project.id.as("projectIdx"),
                        projectInvitation.project.name.as("projectName"),
                        projectInvitation.project.thumbnail.as("thumbnail")
                ))
                .from(projectInvitation)
                .where(projectInvitation.invitee.eq(user), projectInvitation.isAccepted.isFalse())
                .fetch();
    }
}
