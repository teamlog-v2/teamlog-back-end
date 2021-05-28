package com.test.teamlog.repository;

import com.test.teamlog.entity.CommentMention;
import com.test.teamlog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentMentionRepository extends JpaRepository<CommentMention, Long> {

    List<CommentMention> findAllByTargetUser(User user);
}
