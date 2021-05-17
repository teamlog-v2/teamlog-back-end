package com.test.teamlog.repository;

import com.test.teamlog.entity.User;
import com.test.teamlog.entity.UserFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFollowRepository extends JpaRepository<UserFollow, Long> {
    public Optional<UserFollow> findByFromUserAndToUser(User user, User targetUser);

    public List<UserFollow> findByFromUser(User user);
}
