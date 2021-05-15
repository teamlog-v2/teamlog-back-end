package com.test.teamlog.repository;

import com.test.teamlog.entity.Team;
import com.test.teamlog.entity.TeamMember;
import com.test.teamlog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
    List<TeamMember> findByUser(User user);
    List<TeamMember> findByTeam(Team team);
    Optional<TeamMember> findByTeamAndUser(Team team, User user);
}
