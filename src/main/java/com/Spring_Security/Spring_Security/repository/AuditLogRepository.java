package com.Spring_Security.Spring_Security.repository;


import com.Spring_Security.Spring_Security.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByUserIdOrderByTimestampDesc(Long userId);

    List<AuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}

