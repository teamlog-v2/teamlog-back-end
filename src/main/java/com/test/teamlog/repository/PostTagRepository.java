package com.test.teamlog.repository;

import com.test.teamlog.entity.PostLiker;
import com.test.teamlog.entity.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {

}
