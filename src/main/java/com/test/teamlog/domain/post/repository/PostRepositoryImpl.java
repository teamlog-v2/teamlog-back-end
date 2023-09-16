package com.test.teamlog.domain.post.repository;


import com.querydsl.core.types.Ops;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.test.teamlog.domain.post.dto.PostReadByProjectInput;
import com.test.teamlog.domain.post.entity.Post;
import com.test.teamlog.entity.PostMedia;
import com.test.teamlog.global.entity.AccessModifier;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.test.teamlog.domain.post.entity.QPost.post;
import static com.test.teamlog.domain.postlike.entity.QPostLike.postLike;
import static com.test.teamlog.entity.QPostMedia.postMedia;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean existsPostLikeByPostAndUser(Long postIdx, Long userIdx) {
        return jpaQueryFactory
                .select(post.id)
                .from(post)
                .join(postLike)
                .on(postLike.post.eq(post))
                .where(
                        post.id.eq(postIdx),
                        postLike.user.idx.eq(userIdx)
                )
                .fetchFirst() != null;
    }

    @Override
    public List<PostMedia> findAllPostMediaByPostId(Long postId) {
        return jpaQueryFactory
                .select(postMedia)
                .from(post)
                .join(postMedia)
                .on(postMedia.post.eq(post))
                .where(post.id.eq(postId))
                .fetch();
    }

    @Override
    public Page<Post> search(PostReadByProjectInput input, Pageable pageable) {
        final List<Post> content = jpaQueryFactory
                .selectFrom(post)
                .where(
                        post.project.id.eq(input.getProjectId()),
                        hashtagIn(input.getHashtagList()),
                        keywordLike(input.getKeyword()),
                        accessModifierEq(input.getAccessModifier()),
                        copEqAndComparePostId(input.getComparisonOperator(), input.getCursor())
                )
                .orderBy(post.id.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        final Long total = jpaQueryFactory
                .select(post.count())
                .from(post)
                .where(
                        post.project.id.eq(input.getProjectId()),
                        hashtagIn(input.getHashtagList()),
                        keywordLike(input.getKeyword()),
                        accessModifierEq(input.getAccessModifier()),
                        copEqAndComparePostId(input.getComparisonOperator(), input.getCursor())
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression hashtagIn(List<String> hashtagList) {
        return hashtagList == null ? null : post.hashtags.any().name.in(hashtagList);
    }

    private BooleanExpression keywordLike(String keyword) {
        return keyword == null ? null : post.contents.contains(keyword);
    }

    private BooleanExpression accessModifierEq(AccessModifier accessModifier) {
        return accessModifier == null ? null : post.accessModifier.eq(accessModifier);
    }

    private BooleanExpression copEqAndComparePostId(String comparisonOperator, Long cursor) {
        if (!StringUtils.hasText(comparisonOperator) || cursor == null) return null;

        return Expressions.booleanOperation(Ops.EQ, Expressions.constant("<"), Expressions.constant(comparisonOperator))
                .and(post.id.lt(cursor))
                .or(Expressions.booleanOperation(Ops.EQ, Expressions.constant(">"), Expressions.constant(comparisonOperator))
                        .and(post.id.gt(cursor))
                );
    }
}
