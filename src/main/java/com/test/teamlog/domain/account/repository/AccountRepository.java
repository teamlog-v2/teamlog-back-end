package com.test.teamlog.domain.account.repository;

import com.test.teamlog.domain.account.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long>, AccountRepositoryCustom {
    @Query("SELECT u FROM Account u WHERE u.identification LIKE concat('%',:id,'%') AND u.name LIKE concat('%',:name,'%')")
    List<Account> searchAccountByIdentificationAndName(@Param("id") String id, @Param("name") String name);

    Optional<Account> findByIdentification(String identification);

    List<Account> findAllByIdentificationIn(List<String> identificationList);
}
