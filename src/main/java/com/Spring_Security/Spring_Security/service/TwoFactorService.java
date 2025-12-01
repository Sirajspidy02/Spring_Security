package com.Spring_Security.Spring_Security.service;


import com.Spring_Security.Spring_Security.util.TotpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TwoFactorService {

    public boolean verifyTotp(String secret, String code) {
        return TotpUtil.verifyCode(secret, code);
    }

    public String generateQRCode(String email, String secret) {
        // Format: otpauth://totp/{issuer}:{email}?secret={secret}&issuer={issuer}
        // Use QR code library to generate base64 image
        return "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUA..."; // Placeholder
    }
}
