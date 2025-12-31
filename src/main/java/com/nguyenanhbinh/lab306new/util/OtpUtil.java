package com.nguyenanhbinh.lab306new.util;

import java.security.SecureRandom;

public class OtpUtil {

    private static final SecureRandom random = new SecureRandom();

    private OtpUtil() {
        // utility class
    }

    public static String generateOtp4Digits() {
        int otp = 1000 + random.nextInt(9000); // 1000–9999
        return String.valueOf(otp);
    }
}
