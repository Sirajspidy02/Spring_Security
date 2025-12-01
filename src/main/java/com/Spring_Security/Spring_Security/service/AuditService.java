package com.Spring_Security.Spring_Security.service;


import com.Spring_Security.Spring_Security.entity.AuditLog;
import com.Spring_Security.Spring_Security.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    @Transactional
    public void log(Long userId, String action, String ipAddress, String userAgent) {
        AuditLog log = AuditLog.builder()
                .userId(userId)
                .action(action)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .details(action + " performed")
                .build();

        auditLogRepository.save(log);
    }

    @Transactional
    public void logWithDetails(Long userId, String action, String ipAddress,
                               String userAgent, String details) {
        AuditLog log = AuditLog.builder()
                .userId(userId)
                .action(action)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .details(details)
                .build();

        auditLogRepository.save(log);
    }
}
