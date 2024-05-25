package com.app.teamlog.domain.comment.repository;

import com.app.teamlog.domain.account.model.Account;
import com.app.teamlog.domain.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {
    List<Comment> findAllByWriter(Account account);

    Page<Comment> findAllByParentComment(@Param("comment") Comment comment, Pageable pageable);
}
