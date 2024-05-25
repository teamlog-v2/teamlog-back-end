package com.app.teamlog.domain.account.repository;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.app.teamlog.domain.account.model.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.app.teamlog.domain.account.model.QAccount.account;
import static com.app.teamlog.domain.accountfollow.entity.QAccountFollow.accountFollow;
import static com.app.teamlog.domain.projectjoin.entity.QProjectJoin.projectJoin;
import static com.app.teamlog.domain.projectmember.entity.QProjectMember.projectMember;

@Repository
@RequiredArgsConstructor
public class AccountRepositoryImpl implements AccountRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    public boolean isFollow(String accountaccountId, String toAccountId) {
        return jpaQueryFactory
                .selectFrom(accountFollow)
                .where(
                        accountFollow.fromAccount.identification.eq(accountaccountId),
                        accountFollow.toAccount.identification.eq(toAccountId)
                )
                .fetchFirst() != null;
    }

    @Override
    public List<Account> findAccountNotInProjectMember(Long projectId) {
        return jpaQueryFactory
                .selectFrom(account)
                .where(account.idx.notIn(
                        JPAExpressions
                                .select(projectMember.account.idx)
                                .from(projectMember)
                                .where(projectMember.project.id.eq(projectId)
                                )), account.idx.notIn(
                        JPAExpressions
                                .select(projectJoin.account.idx)
                                .from(projectJoin)
                                .where(projectJoin.project.id.eq(projectId))
                )).fetch();
    }
}
