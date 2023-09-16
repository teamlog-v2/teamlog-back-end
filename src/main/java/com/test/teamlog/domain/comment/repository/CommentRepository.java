package com.test.teamlog.domain.comment.repository;

import com.test.teamlog.domain.account.model.User;
import com.test.teamlog.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {
    List<Comment> findAllByWriter(User user);
}
