package com.test.teamlog.repository;

import com.test.teamlog.entity.Post;
import com.test.teamlog.entity.PostMedia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostMediaRepository extends JpaRepository<PostMedia, Long> {
    PostMedia findByStoredFileName(String storedFileName);
    List<PostMedia> findAllByPost(Post post);
}
