package com.test.teamlog.domain.userfollow.repository;

import com.test.teamlog.domain.account.model.User;

import com.test.teamlog.domain.userfollow.entity.UserFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFollowRepository extends JpaRepository<UserFollow, Long> {
    Optional<UserFollow> findByFromUserAndToUser(User user, User targetUser);
    List<UserFollow> findAllByFromUser(User user);
    List<UserFollow> findAllByToUser(User user);
}
