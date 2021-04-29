package com.test.teamlog.repository;

import com.test.teamlog.entity.PostLiker;
import com.test.teamlog.entity.PostMedia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikerRepository extends JpaRepository<PostLiker, Long> {

}
