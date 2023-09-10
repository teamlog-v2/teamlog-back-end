package com.test.teamlog.domain.account.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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

}
