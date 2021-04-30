package com.test.teamlog.repository;

import com.test.teamlog.entity.Comment;
import com.test.teamlog.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostAndParentCommentIsNull(Post post);
}
