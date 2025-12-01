package com.Spring_Security.Spring_Security.service;


import com.Spring_Security.Spring_Security.entity.Otp;
import com.Spring_Security.Spring_Security.enums.TokenType;
import com.Spring_Security.Spring_Security.exception.CustomException;
import com.Spring_Security.Spring_Security.repository.OtpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpService {

    private final OtpRepository otpRepository;

    @Transactional
    public String generateOtp(String email, TokenType type) {

        // Delete old OTPs
        otpRepository.deleteByEmail(email);

        String code = String.format("%06d", new Random().nextInt(999999));

        Otp otp = Otp.builder()
                .email(email)
                .code(code)
                .type(type)
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .build();

        otpRepository.save(otp);

        return code;
    }

    public void validateOtp(String email, String code, TokenType type) {

        Otp otp = otpRepository.findByEmailAndType(email, type)
                .orElseThrow(() -> new CustomException("Invalid OTP"));

        if (otp.isUsed()) {
            throw new CustomException("OTP already used");
        }

        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new CustomException("OTP expired");
        }

        if (!otp.getCode().equals(code)) {
            throw new CustomException("Invalid OTP");
        }

        otp.setUsed(true);
        otpRepository.save(otp);
    }
}
