package com.Spring_Security.Spring_Security.entity;



import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "login_attempts", indexes = {
        @Index(name = "idx_email", columnList = "email"),
        @Index(name = "idx_ip", columnList = "ipAddress")
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String ipAddress;
    private boolean success;
    private String userAgent;

    @Column(updatable = false)
    @Builder.Default
    private LocalDateTime attemptTime = LocalDateTime.now();
}
