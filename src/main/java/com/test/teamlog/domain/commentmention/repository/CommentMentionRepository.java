package com.test.teamlog.domain.commentmention.repository;

import com.test.teamlog.domain.account.model.User;

import com.test.teamlog.entity.CommentMention;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentMentionRepository extends JpaRepository<CommentMention, Long> {

    List<CommentMention> findAllByTargetUser(User user);
}
