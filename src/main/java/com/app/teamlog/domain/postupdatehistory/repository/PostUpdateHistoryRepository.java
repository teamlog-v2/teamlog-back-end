package com.app.teamlog.domain.postupdatehistory.repository;

import com.app.teamlog.domain.post.entity.Post;
import com.app.teamlog.domain.postupdatehistory.entity.PostUpdateHistory;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostUpdateHistoryRepository extends JpaRepository<PostUpdateHistory, Long> {
    public List<PostUpdateHistory> findAllByPost(Post post, Sort sort);
}
