package com.test.teamlog.domain.projectinvitation.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.test.teamlog.domain.project.entity.Project;
import com.test.teamlog.domain.projectinvitation.dto.ProjectInvitationReadInviteeResult;
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
                .where(projectInvitation.isAccepted.isFalse())
                .fetch();
    }
}
