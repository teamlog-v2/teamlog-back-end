package com.test.teamlog.domain.postlike.repository;

import com.test.teamlog.domain.account.model.Account;

import com.test.teamlog.domain.post.entity.Post;
import com.test.teamlog.domain.postlike.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByPostAndAccount(Post post, Account account);
    List<PostLike> findAllByPost(Post post);
}
