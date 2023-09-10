package com.test.teamlog.domain.post.repository;


import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.test.teamlog.domain.post.entity.QPost.post;
import static com.test.teamlog.domain.postlike.entity.QPostLike.postLike;

@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

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

}
