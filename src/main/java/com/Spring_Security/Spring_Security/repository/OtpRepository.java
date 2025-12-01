package com.Spring_Security.Spring_Security.repository;


import com.Spring_Security.Spring_Security.entity.Otp;
import com.Spring_Security.Spring_Security.enums.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OtpRepository extends JpaRepository<Otp, Long> {

    Optional<Otp> findByEmail(String email);

    Optional<Otp> findByEmailAndType(String email, TokenType type);

    void deleteByEmail(String email);

    void deleteByExpiresAtBefore(LocalDateTime dateTime);
}
