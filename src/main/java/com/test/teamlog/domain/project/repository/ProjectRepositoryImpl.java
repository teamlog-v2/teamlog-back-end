package com.test.teamlog.domain.project.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.test.teamlog.domain.posttag.entity.PostTag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.test.teamlog.domain.post.entity.QPost.post;
import static com.test.teamlog.domain.posttag.entity.QPostTag.postTag;
import static com.test.teamlog.domain.project.entity.QProject.project;

@Repository
@RequiredArgsConstructor
public class ProjectRepositoryImpl implements ProjectRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    public List<PostTag> findAllPostTagByProjectId(Long projectId) {
        return jpaQueryFactory
                .select(postTag)
                .from(project)
                .join(post)
                .on(post.project.eq(project))
                .join(postTag)
                .on(postTag.post.eq(post))
                .where(
                        project.id.eq(projectId)
                ).fetch();
    }
}
