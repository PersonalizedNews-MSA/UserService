package com.mini2.user_service.secret.hash;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class SecureHashUtils {
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public static String hash(String password) {
        return encoder.encode(password);
    }

    public static boolean matches(String password, String hashedPassword) {
        return encoder.matches(password, hashedPassword);
    }
}
