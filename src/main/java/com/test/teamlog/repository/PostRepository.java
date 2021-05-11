package com.test.teamlog.repository;

import com.test.teamlog.entity.AccessModifier;
import com.test.teamlog.entity.Post;
import com.test.teamlog.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 카운트
    @Query("SELECT COUNT(p) FROM Post p Where p.project = :project")
    public long getPostsCount(@Param("project") Project project);

    @Query("SELECT p FROM Post p Where p.project = :project")
    public Slice<Post> findAllByProject(@Param("project") Project project, Pageable pageable);

    @Query("SELECT p FROM Post p Where p.project = :project AND ((:cop = '<' and  p.id < :cursor) or (:cop = '>' and  p.id > :cursor))")
    public Slice<Post> findAllByProjectAndCursor(@Param("project") Project project, @Param("cursor") Long cursor,
                                                @Param("cop") String cop, Pageable pageable);

    public List<Post> findAllByLocationIsNotNullAndAccessModifier(AccessModifier accessModifier);
    
    // 카운트
    @Query("SELECT COUNT(p) FROM Post p WHERE (p.contents LIKE concat('%',:keyword,'%') AND p.project = :project)")
    public long getPostsCountByKeyword(@Param("project") Project project, @Param("keyword") String keyword);

    @Query("SELECT p FROM Post p WHERE (p.contents LIKE concat('%',:keyword,'%') AND p.project = :project)")
    public Slice<Post> searchPostsInProject(@Param("project") Project project, @Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.project = :project AND ((:cop = '<' and  p.id < :cursor) or (:cop = '>' and  p.id > :cursor))" +
            "AND (p.contents LIKE concat('%',:keyword,'%'))")
    public Slice<Post> searchPostsInProjectByCursor(@Param("project") Project project, @Param("cursor") Long cursor,
                                                   @Param("keyword") String keyword, @Param("cop") String cop, Pageable pageable);

    // 카운트
    @Query("SELECT p FROM Post p, PostTag h WHERE h.post = p AND h.name IN (:names) AND p.project = :project GROUP BY p.id")
    public Page<Post> getPostsCountByHashTag(@Param("project") Project project, @Param("names") List<String> names, Pageable pageable);

    @Query("SELECT p FROM Post p, PostTag h WHERE h.post = p AND h.name IN (:names) AND p.project = :project GROUP BY p.id")
    public Slice<Post> getPostsInProjectByHashTag(@Param("project") Project project, @Param("names") List<String> names, Pageable pageable);

    @Query("SELECT p FROM Post p, PostTag h WHERE p.project = :project AND ((:cop = '<' and  p.id < :cursor) or (:cop = '>' and  p.id > :cursor)) " +
            "AND h.post = p AND h.name IN (:names) GROUP BY p.id")
    public Slice<Post> getPostsInProjectByHashTagAndCursor(@Param("project") Project project, @Param("cursor") Long cursor,
                                                          @Param("names") List<String> names, @Param("cop") String cop, Pageable pageable);


    // 카운트
    @Query("SELECT p FROM Post p, PostTag h WHERE h.post = p AND h.name IN (:names) AND (p.contents LIKE concat('%',:keyword,'%') AND p.project = :project) GROUP BY p.id")
    public Page<Post> getPostsCountByHashtagAndKeyword(@Param("project") Project project, @Param("names") List<String> names,
                                                              @Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Post p, PostTag h WHERE h.post = p AND h.name IN (:names) AND (p.contents LIKE concat('%',:keyword,'%') AND p.project = :project) GROUP BY p.id")
    public Slice<Post> searchPostsInProjectByHashtagAndKeyword(@Param("project") Project project, @Param("names") List<String> names,
                                                              @Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Post p, PostTag h WHERE p.project = :project AND ((:cop = '<' and  p.id < :cursor) or (:cop = '>' and  p.id > :cursor)) " +
            "AND h.post = p AND h.name IN (:names) AND (p.contents LIKE concat('%',:keyword,'%')) GROUP BY p.id")
    public Slice<Post> searchPostsInProjectByHashtagAndKeywordAndCursor(@Param("project") Project project, @Param("cursor") Long cursor,
                                                                       @Param("names") List<String> names, @Param("keyword") String keyword,
                                                                       @Param("cop") String cop, Pageable pageable);
}
