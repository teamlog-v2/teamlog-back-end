package com.test.teamlog.repository;

import com.test.teamlog.entity.Team;
import com.test.teamlog.entity.TeamFollower;
import com.test.teamlog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamFollowerRepository extends JpaRepository<TeamFollower, Long> {
    List<TeamFollower> findAllByUser(User user);
    List<TeamFollower> findAllByTeam(Team team);
    Optional<TeamFollower> findByTeamAndUser(Team team,User user);

}
