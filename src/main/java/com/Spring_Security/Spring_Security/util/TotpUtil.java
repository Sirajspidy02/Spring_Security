//package com.Spring_Security.Spring_Security.util;
//
//import javax.crypto.Mac;
//import javax.crypto.spec.SecretKeySpec;
//import java.security.SecureRandom;
//import java.util.Base64;
//
//public class TotpUtil {
//
//    /**
//     * Generate a random secret key encoded in Base64
//     */
//    public static String generateSecret() {
//        SecureRandom random = new SecureRandom();
//        byte[] bytes = new byte[20]; // 160-bit key
//        random.nextBytes(bytes);
//
//        // Encode to Base64 without padding
//        return Base64.getEncoder().withoutPadding().encodeToString(bytes);
//    }
//
//    /**
//     * Verify a TOTP code for a given secret
//     */
//    public static boolean verifyCode(String secret, String code) {
//        long currentTime = System.currentTimeMillis() / 1000 / 30;
//
//        // Check previous, current, and next time step (to allow small clock skew)
//        for (int i = -1; i <= 1; i++) {
//            if (generateTOTP(secret, currentTime + i).equals(code)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    /**
//     * Generate TOTP code for a given secret and time step
//     */
//    private static String generateTOTP(String secret, long time) {
//        try {
//            byte[] key = Base64.getDecoder().decode(secret);
//
//            byte[] data = new byte[8];
//            long value = time;
//            for (int i = 8; i-- > 0; value >>>= 8) {
//                data[i] = (byte) value;
//            }
//
//            Mac mac = Mac.getInstance("HmacSHA1");
//            mac.init(new SecretKeySpec(key, "HmacSHA1"));
//            byte[] hash = mac.doFinal(data);
//
//            int offset = hash[hash.length - 1] & 0xF;
//            long truncatedHash = 0;
//            for (int i = 0; i < 4; i++) {
//                truncatedHash <<= 8;
//                truncatedHash |= (hash[offset + i] & 0xFF);
//            }
//
//            truncatedHash &= 0x7FFFFFFF;
//            truncatedHash %= 1000000;
//
//            return String.format("%06d", truncatedHash);
//        } catch (Exception e) {
//            throw new RuntimeException("Error generating TOTP", e);
//        }
//    }
//
//    // Optional test method
//    public static void main(String[] args) {
//        String secret = generateSecret();
//        System.out.println("Secret: " + secret);
//
//        String code = generateTOTP(secret, System.currentTimeMillis() / 1000 / 30);
//        System.out.println("TOTP Code: " + code);
//
//        System.out.println("Verification: " + verifyCode(secret, code));
//    }
//}

package com.Spring_Security.Spring_Security.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

public class TotpUtil {

    /**
     * Generate a random secret key encoded in Base64
     */
    public static String generateSecret() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20]; // 160-bit key
        random.nextBytes(bytes);
        return Base64.getEncoder().withoutPadding().encodeToString(bytes);
    }

    /**
     * Verify a TOTP code for a given secret
     */
    public static boolean verifyCode(String secret, String code) {
        long currentTime = System.currentTimeMillis() / 1000 / 30;

        for (int i = -1; i <= 1; i++) {
            if (generateTOTP(secret, currentTime + i).equals(code)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Generate TOTP code for a given secret and time step
     */
    private static String generateTOTP(String secret, long time) {
        try {
            byte[] key = Base64.getDecoder().decode(secret);
            byte[] data = new byte[8];
            long value = time;
            for (int i = 8; i-- > 0; value >>>= 8) {
                data[i] = (byte) value;
            }

            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(key, "HmacSHA1"));
            byte[] hash = mac.doFinal(data);

            int offset = hash[hash.length - 1] & 0xF;
            long truncatedHash = 0;
            for (int i = 0; i < 4; i++) {
                truncatedHash <<= 8;
                truncatedHash |= (hash[offset + i] & 0xFF);
            }

            truncatedHash &= 0x7FFFFFFF;
            truncatedHash %= 1000000;

            return String.format("%06d", truncatedHash);
        } catch (Exception e) {
            throw new RuntimeException("Error generating TOTP", e);
        }
    }
}

