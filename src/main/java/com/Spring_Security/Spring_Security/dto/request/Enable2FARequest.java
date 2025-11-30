    package com.Spring_Security.Spring_Security.dto.request;


    import jakarta.validation.constraints.NotBlank;
    import lombok.Data;

    @Data
    public class Enable2FARequest {
        @NotBlank(message = "TOTP code is required")
        private String totpCode;
    }
