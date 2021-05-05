package com.test.teamlog.repository;

import com.test.teamlog.entity.AccessModifier;
import com.test.teamlog.entity.Post;
import com.test.teamlog.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT COUNT(p) FROM Post p WHERE p.project = :project")
    public int getPostCount(@Param("project") Project project);

    @Query("SELECT p FROM Post p Where p.project = :project")
    public Page<Post> findAllByProject(@Param("project") Project project, Pageable pageable);

    public List<Post> findAllByLocationIsNotNullAndAccessModifier(AccessModifier accessModifier);

    @Query("SELECT p FROM Post p WHERE (p.contents LIKE concat('%',:keyword,'%') AND p.project = :project)")
    public Page<Post> searchPostsInProject(@Param("project") Project project, @Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Post p, PostTag h WHERE h.post = p AND h.name IN (:names) AND p.project = :project GROUP BY p.id")
    public Page<Post> getPostsInProjectByHashTag(@Param("project") Project project, @Param("names") List<String> names, Pageable pageable);
}
