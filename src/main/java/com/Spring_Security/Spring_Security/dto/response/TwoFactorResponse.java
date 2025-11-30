package com.Spring_Security.Spring_Security.dto.response;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TwoFactorResponse {
    private String secret;
    private String qrCode;
    private String message;
}



