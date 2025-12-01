//package com.Spring_Security.Spring_Security.service;
//
//
//
//
//import com.Spring_Security.Spring_Security.Security.JwtProvider;
//import com.Spring_Security.Spring_Security.repository.RefreshTokenRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.Instant;
//
//@Service
//@RequiredArgsConstructor
//public class RefreshTokenService {
//
//    private final RefreshTokenRepository repository;
//    private final JwtProvider jwtProvider;
//
//    @Transactional
//    public void revokeAllUserTokens(Long userId) {
//        repository.findAllByUserId(userId).forEach(token -> {
//            token.setRevoked(true);
//            repository.save(token);
//        });
//    }
//
//    @Transactional
//    public void deleteExpiredTokens() {
//        repository.deleteByExpiryDateBefore(Instant.now());
//    }
//}


package com.Spring_Security.Spring_Security.service;

import com.Spring_Security.Spring_Security.entity.RefreshToken;
import com.Spring_Security.Spring_Security.entity.User;
import com.Spring_Security.Spring_Security.repository.RefreshTokenRepository;
import com.Spring_Security.Spring_Security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repository;
    private final UserRepository userRepository;

    public RefreshToken createRefreshToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(Instant.now().plusMillis(86400000)); // 1 day

        return repository.save(token);
    }

    public boolean validateRefreshToken(String token) {
        RefreshToken refreshToken = repository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (refreshToken.isRevoked())
            return false;

        return refreshToken.getExpiryDate().isAfter(Instant.now());
    }

    public User getUserFromRefreshToken(String token) {
        return repository.findByToken(token)
                .map(RefreshToken::getUser)
                .orElseThrow(() -> new RuntimeException("Invalid token"));
    }

    public void revokeUserTokens(Long userId) {
        repository.findAllByUserId(userId).forEach(rt -> {
            rt.setRevoked(true);
            repository.save(rt);
        });
    }

    public void deleteExpiredTokens() {
        repository.deleteByExpiryDateBefore(Instant.now());
    }
}
