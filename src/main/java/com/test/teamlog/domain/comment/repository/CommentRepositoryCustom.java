package com.test.teamlog.domain.comment.repository;

import com.test.teamlog.domain.comment.entity.Comment;
import com.test.teamlog.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

public interface CommentRepositoryCustom {
    Page<Comment> findParentCommentListByPost(@Param("post") Post post, Pageable pageable);
}
