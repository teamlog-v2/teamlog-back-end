package com.test.teamlog.repository;

import com.test.teamlog.entity.AccessModifier;
import com.test.teamlog.entity.Post;
import com.test.teamlog.entity.Project;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT COUNT(p) FROM Post p WHERE p.project = :project")
    public int getPostCount(@Param("project") Project project);

    @Query("SELECT distinct p FROM Post p left join fetch p.hashtags order by p.createTime desc")
    public List<Post> findAllByProject(Project project);

    public List<Post> findAllByLocationIsNotNullAndAccessModifier(AccessModifier accessModifier);

    @Query("SELECT distinct p FROM Post p left join fetch p.hashtags WHERE (p.contents like concat('%',:keyword,'%') and p.project = :project) order by p.createTime desc")
    public List<Post> searchPostsInProject(@Param("project") Project project, @Param("keyword") String keyword);
}
