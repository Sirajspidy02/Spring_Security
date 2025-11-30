package com.Spring_Security.Spring_Security.controller;


//import com.Spring_Security.Spring_Security.dto.request.*;
import com.Spring_Security.Spring_Security.dto.request.*;
import com.Spring_Security.Spring_Security.dto.response.ApiResponse;
import com.Spring_Security.Spring_Security.dto.response.AuthResponse;
import com.Spring_Security.Spring_Security.dto.response.TwoFactorResponse;
import com.Spring_Security.Spring_Security.service.AuthService;
import com.Spring_Security.Spring_Security.util.IpUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> signup(
            @Valid @RequestBody SignupRequest request,
            HttpServletRequest httpRequest) {

        String ipAddress = IpUtil.getClientIp(httpRequest);
        authService.signup(request, ipAddress);

        return ResponseEntity.ok(new ApiResponse(
                "Signup successful! Please check your email to verify your account."
        ));
    }

    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse> verifyEmail(
            @Valid @RequestBody OtpVerifyRequest request) {

        authService.verifyEmail(request);
        return ResponseEntity.ok(new ApiResponse("Email verified successfully!"));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {

        String ipAddress = IpUtil.getClientIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");

        AuthResponse response = authService.login(request, ipAddress, userAgent);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            @Valid @RequestBody TokenRefreshRequest request) {

        AuthResponse response = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        authService.forgotPassword(request);
        return ResponseEntity.ok(new ApiResponse(
                "If the email exists, a password reset OTP has been sent."
        ));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        authService.resetPassword(request);
        return ResponseEntity.ok(new ApiResponse("Password reset successful!"));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(HttpServletRequest request) {
        String token = extractToken(request);
        authService.logout(token);
        return ResponseEntity.ok(new ApiResponse("Logged out successfully"));
    }

    @PostMapping("/enable-2fa")
    public ResponseEntity<TwoFactorResponse> enable2FA(HttpServletRequest request) {
        String email = extractEmailFromToken(request);
        TwoFactorResponse response = authService.enable2FA(email);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-2fa")
    public ResponseEntity<ApiResponse> verify2FA(
            @Valid @RequestBody Enable2FARequest request,
            HttpServletRequest httpRequest) {

        String email = extractEmailFromToken(httpRequest);
        authService.verify2FA(email, request.getTotpCode());

        return ResponseEntity.ok(new ApiResponse("2FA enabled successfully!"));
    }

    @PostMapping("/disable-2fa")
    public ResponseEntity<ApiResponse> disable2FA(
            @Valid @RequestBody Enable2FARequest request,
            HttpServletRequest httpRequest) {

        String email = extractEmailFromToken(httpRequest);
        authService.disable2FA(email, request.getTotpCode());

        return ResponseEntity.ok(new ApiResponse("2FA disabled successfully!"));
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private String extractEmailFromToken(HttpServletRequest request) {
        // This would be implemented in JwtProvider
        return "extracted-email@example.com"; // Placeholder
    }
}

