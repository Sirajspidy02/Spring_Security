package com.Spring_Security.Spring_Security.service;




import com.Spring_Security.Spring_Security.entity.LoginAttempt;
import com.Spring_Security.Spring_Security.repository.LoginAttemptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LoginAttemptService {

    private final LoginAttemptRepository loginAttemptRepository;

    @Transactional
    public void recordSuccess(String email, String ipAddress, String userAgent) {
        LoginAttempt attempt = LoginAttempt.builder()
                .email(email)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .success(true)
                .build();

        loginAttemptRepository.save(attempt);
    }

    @Transactional
    public void recordFailure(String email, String ipAddress, String userAgent) {
        LoginAttempt attempt = LoginAttempt.builder()
                .email(email)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .success(false)
                .build();

        loginAttemptRepository.save(attempt);
    }

    public long getFailedAttemptsCount(String email, int minutes) {
        LocalDateTime since = LocalDateTime.now().minusMinutes(minutes);
        return loginAttemptRepository.countByEmailAndSuccessAndAttemptTimeAfter(
                email, false, since
        );
    }
}
