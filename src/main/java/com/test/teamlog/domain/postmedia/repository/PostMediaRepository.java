package com.test.teamlog.domain.postmedia.repository;

import com.test.teamlog.domain.post.entity.Post;
import com.test.teamlog.domain.postmedia.entity.PostMedia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostMediaRepository extends JpaRepository<PostMedia, Long> {
    PostMedia findByStoredFileName(String storedFileName);
    List<PostMedia> findAllByPost(Post post);
    List<PostMedia> findAllByIdIn(List<Long> idList);
    void deleteAllByIdIn(List<Long> idList);
}
