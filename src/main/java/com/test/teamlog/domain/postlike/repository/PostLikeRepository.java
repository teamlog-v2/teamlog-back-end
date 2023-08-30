package com.test.teamlog.domain.postlike.repository;

import com.test.teamlog.domain.account.model.User;

import com.test.teamlog.domain.post.entity.Post;
import com.test.teamlog.domain.postlike.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByPostAndUser(Post post, User user);
    List<PostLike> findAllByPost(Post post);
}
