package com.test.teamlog.repository;

import com.test.teamlog.entity.CommentMention;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentMentionRepository extends JpaRepository<CommentMention, Long> {
}
