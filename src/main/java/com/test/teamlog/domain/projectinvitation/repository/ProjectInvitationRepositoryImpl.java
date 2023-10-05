package com.test.teamlog.domain.projectinvitation.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProjectInvitationRepositoryImpl implements ProjectInvitationCustom {
    private final JPAQueryFactory jpaQueryFactory;
}
