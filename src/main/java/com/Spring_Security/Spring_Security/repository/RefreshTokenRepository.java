package com.Spring_Security.Spring_Security.repository;


import com.Spring_Security.Spring_Security.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findAllByUserId(Long userId);

    void deleteByUserId(Long userId);

    void deleteByExpiryDateBefore(Instant date);
}
