package com.Spring_Security.Spring_Security.service;


import com.Spring_Security.Spring_Security.Security.JwtProvider;
import com.Spring_Security.Spring_Security.dto.request.*;
import com.Spring_Security.Spring_Security.dto.response.AuthResponse;
import com.Spring_Security.Spring_Security.dto.response.TwoFactorResponse;
import com.Spring_Security.Spring_Security.dto.response.UserResponse;
import com.Spring_Security.Spring_Security.entity.RefreshToken;
import com.Spring_Security.Spring_Security.entity.User;
import com.Spring_Security.Spring_Security.enums.Provider;
import com.Spring_Security.Spring_Security.enums.RoleType;
import com.Spring_Security.Spring_Security.enums.TokenType;
import com.Spring_Security.Spring_Security.exception.AccountLockedException;
import com.Spring_Security.Spring_Security.exception.CustomException;
import com.Spring_Security.Spring_Security.exception.InvalidTokenException;
import com.Spring_Security.Spring_Security.exception.ResourceNotFoundException;
import com.Spring_Security.Spring_Security.repository.RefreshTokenRepository;
import com.Spring_Security.Spring_Security.repository.UserRepository;
import com.Spring_Security.Spring_Security.util.TotpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final OtpService otpService;
    private final EmailService emailService;
    private final TwoFactorService twoFactorService;
    private final LoginAttemptService loginAttemptService;
    private final AuditService auditService;

    @Transactional
    public void signup(SignupRequest request, String ipAddress) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException("Email already registered");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .mobile(request.getMobile())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(RoleType.USER)
                .provider(Provider.LOCAL)
                .enabled(false)
                .accountNonLocked(true)
                .build();

        userRepository.save(user);

        String otp = otpService.generateOtp(user.getEmail(), TokenType.SIGNUP);
        emailService.sendOtpEmail(user.getEmail(), otp, "Email Verification");

        auditService.log(user.getId(), "SIGNUP", ipAddress, "User registered");
    }

    @Transactional
    public void verifyEmail(OtpVerifyRequest request) {
        otpService.validateOtp(request.getEmail(), request.getOtp(), TokenType.SIGNUP);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setEnabled(true);
        userRepository.save(user);

        emailService.sendWelcomeEmail(user.getEmail(), user.getFullName());
    }

    @Transactional
    public AuthResponse login(LoginRequest request, String ipAddress, String userAgent) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException("Invalid credentials"));

        // Check account lock
        if (!user.isAccountNonLocked()) {
            if (user.getLockTime() != null &&
                    user.getLockTime().plusHours(24).isAfter(LocalDateTime.now())) {
                throw new AccountLockedException("Account is locked. Try again later.");
            }
            user.setAccountNonLocked(true);
            user.setFailedAttempts(0);
            user.setLockTime(null);
            userRepository.save(user);
        }

        // Check brute force
        long failedAttempts = loginAttemptService.getFailedAttemptsCount(request.getEmail(), 30);
        if (failedAttempts >= 5) {
            throw new CustomException("Too many failed attempts. Try again later.");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (Exception e) {
            user.setFailedAttempts(user.getFailedAttempts() + 1);

            if (user.getFailedAttempts() >= 5) {
                user.setAccountNonLocked(false);
                user.setLockTime(LocalDateTime.now());
            }

            userRepository.save(user);
            loginAttemptService.recordFailure(request.getEmail(), ipAddress, userAgent);

            throw new CustomException("Invalid credentials");
        }

        // Reset failed attempts
        user.setFailedAttempts(0);
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        loginAttemptService.recordSuccess(request.getEmail(), ipAddress, userAgent);
        auditService.log(user.getId(), "LOGIN", ipAddress, userAgent);

        // Check 2FA
        if (user.isTwoFactorEnabled()) {
            if (request.getTotpCode() == null || request.getTotpCode().isEmpty()) {
                return AuthResponse.builder()
                        .requiresTwoFactor(true)
                        .message("2FA code required")
                        .build();
            }

            boolean valid = twoFactorService.verifyTotp(user.getTotpSecret(), request.getTotpCode());
            if (!valid) {
                throw new CustomException("Invalid 2FA code");
            }
        }

        String accessToken = jwtProvider.generateAccessToken(user.getEmail(), user.getRole().name());
        String refreshToken = jwtProvider.generateRefreshToken(user.getEmail());

        saveRefreshToken(user, refreshToken, ipAddress, userAgent);

        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .mobile(user.getMobile())
                .role(user.getRole().name())
                .twoFactorEnabled(user.isTwoFactorEnabled())
                .build();

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(userResponse)
                .expiresIn(900000L)
                .message("Login successful")
                .build();
    }

    @Transactional
    public AuthResponse refreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));

        if (refreshToken.isRevoked()) {
            throw new InvalidTokenException("Token has been revoked");
        }

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            throw new InvalidTokenException("Refresh token expired");
        }

        User user = refreshToken.getUser();
        String newAccessToken = jwtProvider.generateAccessToken(user.getEmail(), user.getRole().name());

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(token)
                .expiresIn(900000L)
                .build();
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);

        if (user != null) {
            String otp = otpService.generateOtp(user.getEmail(), TokenType.PASSWORD_RESET);
            emailService.sendPasswordResetEmail(user.getEmail(), otp);
        }
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        otpService.validateOtp(request.getEmail(), request.getOtp(), TokenType.PASSWORD_RESET);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        auditService.log(user.getId(), "PASSWORD_RESET", "N/A", "Password reset via OTP");
    }

    @Transactional
    public void logout(String token) {
        String email = jwtProvider.extractUsername(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        refreshTokenRepository.findAllByUserId(user.getId()).forEach(rt -> {
            rt.setRevoked(true);
            refreshTokenRepository.save(rt);
        });

        auditService.log(user.getId(), "LOGOUT", "N/A", "User logged out");
    }

    @Transactional
    public TwoFactorResponse enable2FA(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String secret = TotpUtil.generateSecret();
        user.setTotpSecret(secret);
        userRepository.save(user);

        String qrCode = twoFactorService.generateQRCode(email, secret);

        return TwoFactorResponse.builder()
                .secret(secret)
                .qrCode(qrCode)
                .message("Scan QR code with Google Authenticator")
                .build();
    }

    @Transactional
    public void verify2FA(String email, String code) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean valid = twoFactorService.verifyTotp(user.getTotpSecret(), code);
        if (!valid) {
            throw new CustomException("Invalid 2FA code");
        }

        user.setTwoFactorEnabled(true);
        userRepository.save(user);
    }

    @Transactional
    public void disable2FA(String email, String code) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean valid = twoFactorService.verifyTotp(user.getTotpSecret(), code);
        if (!valid) {
            throw new CustomException("Invalid 2FA code");
        }

        user.setTwoFactorEnabled(false);
        user.setTotpSecret(null);
        userRepository.save(user);
    }

    private void saveRefreshToken(User user, String token, String ipAddress, String userAgent) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .user(user)
                .expiryDate(Instant.now().plusMillis(604800000))
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();

        refreshTokenRepository.save(refreshToken);
    }
}

