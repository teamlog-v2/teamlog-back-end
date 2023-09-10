package com.test.teamlog.domain.post.repository;


import com.test.teamlog.entity.PostMedia;

import java.util.List;

public interface PostRepositoryCustom {
    boolean existsPostLikeByPostAndUser(Long postId, Long userId);

    List<PostMedia> findAllPostMediaByPostId(Long postId);
}
