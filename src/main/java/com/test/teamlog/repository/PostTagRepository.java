package com.test.teamlog.repository;

import com.test.teamlog.entity.Post;
import com.test.teamlog.entity.PostTag;
import com.test.teamlog.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {
}
