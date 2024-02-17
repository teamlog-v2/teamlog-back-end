package com.test.teamlog.domain.project.repository;

import com.test.teamlog.domain.account.model.Account;

import com.test.teamlog.domain.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>, ProjectRepositoryCustom {
    @Query("SELECT p FROM Project p WHERE p.name LIKE concat('%',:name,'%')")
    List<Project> searchProjectByName(@Param("name") String name);

    // FIXME: QueryDsl 도입 후 order by는 파라미터로 받기
    @Query("select j.project from ProjectMember j where j.account = :account order by j.project.updateTime desc")
    List<Project> findProjectByAccount(@Param("account") Account account);
}
