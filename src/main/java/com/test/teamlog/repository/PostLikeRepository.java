package com.test.teamlog.repository;

import com.test.teamlog.domain.account.model.User;

import com.test.teamlog.entity.Post;
import com.test.teamlog.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByPostAndUser(Post post, User user);
    List<PostLike> findAllByPost(Post post);
}
