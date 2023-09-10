package com.test.teamlog.domain.post.repository;


import com.test.teamlog.domain.postupdatehistory.entity.PostUpdateHistory;

import java.util.List;

public interface PostRepositoryCustom {
    boolean existsPostLikeByPostAndUser(Long postId, Long userId);

    List<PostUpdateHistory> findAllPostUpdateHistoryByPostId(Long postId);
}
