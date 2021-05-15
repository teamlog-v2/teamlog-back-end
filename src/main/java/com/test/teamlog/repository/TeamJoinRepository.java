package com.test.teamlog.repository;

import com.test.teamlog.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamJoinRepository extends JpaRepository<TeamJoin, Long> {
    Optional<TeamJoin> findByTeamAndUser(Team team, User user);
    List<TeamJoin> findAllByTeamAndIsAcceptedFalseAndIsInvitedTrue(Team team);
    List<TeamJoin> findAllByTeamAndIsAcceptedTrueAndIsInvitedFalse(Team team);
    List<TeamJoin> findAllByUserAndIsAcceptedFalseAndIsInvitedTrue(User user);
    List<TeamJoin> findAllByUserAndIsAcceptedTrueAndIsInvitedFalse(User user);
}
