package com.test.teamlog.domain.account.repository;

import com.test.teamlog.domain.account.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<User, String>, AccountRepositoryCustom {
    @Query("SELECT u FROM User u WHERE u.identification LIKE concat('%',:id,'%') AND u.name LIKE concat('%',:name,'%')")
    List<User> searchUserByIdentificationAndName(@Param("id") String id, @Param("name") String name);

    @Query("SELECT u FROM User u WHERE u.identification not in (select m.user.identification from ProjectMember m where m.project.id = :projectId)" +
            "and u.identification not in (select j.user.identification from ProjectJoin j where j.project.id = :projectId)")
    List<User> getUsersNotInProjectMember(@Param("projectId") Long projectId);

    Optional<User> findByIdentification(String identification);

    List<User> findAllByIdentificationIn(List<String> identificationList);
}
