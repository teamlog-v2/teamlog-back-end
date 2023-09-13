package com.test.teamlog.domain.account.repository;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.test.teamlog.domain.account.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.test.teamlog.domain.account.model.QUser.user;
import static com.test.teamlog.domain.projectjoin.entity.QProjectJoin.projectJoin;
import static com.test.teamlog.domain.projectmember.entity.QProjectMember.projectMember;
import static com.test.teamlog.domain.userfollow.entity.QUserFollow.userFollow;

@Repository
@RequiredArgsConstructor
public class AccountRepositoryImpl implements AccountRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    public boolean isFollow(String fromUserId, String toUserId) {
        return jpaQueryFactory
                .selectFrom(userFollow)
                .where(
                        userFollow.fromUser.identification.eq(fromUserId),
                        userFollow.toUser.identification.eq(toUserId)
                )
                .fetchFirst() != null;
    }

    @Override
    public List<User> findUsersNotInProjectMember(Long projectId) {
        return jpaQueryFactory
                .selectFrom(user)
                .where(user.idx.notIn(
                        JPAExpressions
                                .select(projectMember.user.idx)
                                .from(projectMember)
                                .where(projectMember.project.id.eq(projectId)
                                )), user.idx.notIn(
                        JPAExpressions
                                .select(projectJoin.user.idx)
                                .from(projectJoin)
                                .where(projectJoin.project.id.eq(projectId))
                )).fetch();
    }
}
