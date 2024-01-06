package com.test.teamlog.domain.comment.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.test.teamlog.domain.comment.entity.Comment;
import com.test.teamlog.domain.post.entity.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.test.teamlog.domain.comment.entity.QComment.comment;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Comment> findParentCommentListByPost(Post post, Pageable pageable) {
        final List<Comment> content = jpaQueryFactory.selectFrom(comment)
                .where(comment.post.eq(post), comment.parentComment.isNull())
                .orderBy(comment.createTime.desc())
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetch();

        Long total = jpaQueryFactory
                .select(comment.count())
                .from(comment)
                .where(comment.post.eq(post), comment.parentComment.isNull())
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}
