package com.Spring_Security.Spring_Security.repository;


import com.Spring_Security.Spring_Security.entity.LoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Long> {

    long countByEmailAndSuccessAndAttemptTimeAfter(
            String email,
            boolean success,
            LocalDateTime since
    );
}
