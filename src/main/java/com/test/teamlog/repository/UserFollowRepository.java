package com.test.teamlog.repository;

import com.test.teamlog.entity.User;
import com.test.teamlog.entity.UserFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserFollowRepository extends JpaRepository<UserFollow, Long> {
    public UserFollow findByFromUserAndToUser(User user, User targetUser);
}
