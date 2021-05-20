package com.test.teamlog.repository;

import com.test.teamlog.entity.Post;
import com.test.teamlog.entity.PostTag;
import com.test.teamlog.entity.PostUpdateHistory;
import com.test.teamlog.entity.Project;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostUpdateHistoryRepository extends JpaRepository<PostUpdateHistory, Long> {

    public List<PostUpdateHistory> findAllByPost(Post post, Sort sort);
}
