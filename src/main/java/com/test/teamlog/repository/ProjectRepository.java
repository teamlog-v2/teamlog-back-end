package com.test.teamlog.repository;

import com.test.teamlog.entity.Project;
import com.test.teamlog.entity.Team;
import com.test.teamlog.entity.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    // 카운트
    @Query("SELECT COUNT(p) FROM Project p Where p.team = :team")
    long getProjectCount(@Param("team") Team team);

    @Query("SELECT p FROM Project p WHERE p.name LIKE concat('%',:name,'%')")
    List<Project> searchProjectByName(@Param("name") String name);

    List<Project> findAllByTeam(Team team);

    @Query("select j.project from ProjectMember j where j.user = :user")
    List<Project> getProjectByUser(@Param("user") User user);

}
