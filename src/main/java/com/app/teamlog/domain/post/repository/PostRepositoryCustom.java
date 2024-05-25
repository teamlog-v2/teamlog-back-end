package com.app.teamlog.domain.post.repository;


import com.app.teamlog.domain.post.dto.PostReadByProjectInput;
import com.app.teamlog.domain.post.entity.Post;
import com.app.teamlog.domain.postmedia.entity.PostMedia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostRepositoryCustom {
    boolean existsPostLikeByPostAndAccount(Long postId, Long accountId);

    List<PostMedia> findAllPostMediaByPostId(Long postId);

    Page<Post> search(PostReadByProjectInput input, Pageable pageable);
}
