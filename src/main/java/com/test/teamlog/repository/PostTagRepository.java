package com.test.teamlog.repository;

import com.test.teamlog.entity.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {
    @Query("SELECT p FROM PostTag p join fetch p.post WHERE p.name IN (:names) group by p.post")
    public List<PostTag> getPostTagByNames(@Param("names") List<String> names);
}
