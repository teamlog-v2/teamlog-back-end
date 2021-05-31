package com.test.teamlog.repository;

import com.test.teamlog.entity.Project;
import com.test.teamlog.entity.Team;
import com.test.teamlog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    @Query("SELECT t FROM Team t WHERE t.name LIKE concat('%',:name,'%')")
    List<Team> searchTeamByName(@Param("name") String name);

    @Query("select j.team from TeamMember j where j.user = :user")
    List<Team> getTeamsByUser(@Param("user") User user);
}
