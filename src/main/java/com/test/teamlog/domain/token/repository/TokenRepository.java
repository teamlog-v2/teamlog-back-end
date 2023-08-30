package com.test.teamlog.domain.token.repository;

import com.test.teamlog.domain.token.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByIdentification(String identification);
}
