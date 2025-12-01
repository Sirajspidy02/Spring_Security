package com.Spring_Security.Spring_Security.service;



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendOtpEmail(String to, String otp, String subject) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@yourapp.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(buildOtpEmailBody(otp));

            mailSender.send(message);
            log.info("OTP email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to: {}", to, e);
        }
    }

    @Async
    public void sendWelcomeEmail(String to, String name) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@yourapp.com");
            message.setTo(to);
            message.setSubject("Welcome to Our Platform!");
            message.setText("Hi " + name + ",\n\nWelcome to our platform!\n\nBest Regards,\nTeam");

            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send welcome email", e);
        }
    }

    @Async
    public void sendPasswordResetEmail(String to, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("noreply@yourapp.com");
            message.setTo(to);
            message.setSubject("Password Reset Request");
            message.setText(buildPasswordResetBody(otp));

            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send password reset email", e);
        }
    }

    private String buildOtpEmailBody(String otp) {
        return String.format("""
                Your OTP code is: %s
                
                This code will expire in 10 minutes.
                
                If you didn't request this, please ignore this email.
                
                Best Regards,
                Your App Team
                """, otp);
    }

    private String buildPasswordResetBody(String otp) {
        return String.format("""
                You requested a password reset.
                
                Your OTP code is: %s
                
                This code will expire in 10 minutes.
                
                If you didn't request this, please contact support immediately.
                
                Best Regards,
                Your App Team
                """, otp);
    }
}
