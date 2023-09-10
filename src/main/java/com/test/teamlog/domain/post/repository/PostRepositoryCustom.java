package com.test.teamlog.domain.post.repository;


public interface PostRepositoryCustom {
    boolean existsPostLikeByPostAndUser(Long postId, Long userId);
}
