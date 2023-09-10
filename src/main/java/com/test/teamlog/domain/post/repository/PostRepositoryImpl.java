package com.test.teamlog.domain.post.repository;


import com.querydsl.jpa.impl.JPAQueryFactory;
import com.test.teamlog.domain.postupdatehistory.entity.PostUpdateHistory;
import com.test.teamlog.entity.PostMedia;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.test.teamlog.domain.post.entity.QPost.post;
import static com.test.teamlog.domain.postlike.entity.QPostLike.postLike;
import static com.test.teamlog.domain.postupdatehistory.entity.QPostUpdateHistory.postUpdateHistory;
import static com.test.teamlog.entity.QPostMedia.postMedia;

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

    public List<PostUpdateHistory> findAllPostUpdateHistoryByPostId(Long postId) {
        return jpaQueryFactory
                .selectFrom(postUpdateHistory)
                .where(postUpdateHistory.post.id.eq(postId))
                .orderBy(postUpdateHistory.id.asc())
                .fetch();
    }

    public List<PostMedia> findAllPostMediaByPostId(Long postId) {
        return jpaQueryFactory
                .select(postMedia)
                .from(post)
                .join(postMedia)
                .on(postMedia.post.eq(post))
                .where(post.id.eq(postId))
                .fetch();
    }
}
