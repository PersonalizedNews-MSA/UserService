package com.mini2.user_service.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import java.time.Duration;

public class CookieUtils {
    // refreshToken 쿠키 값을 추출
    public static String extractRefreshToken(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        throw new IllegalArgumentException("Refresh Token이 존재하지 않습니다.");
    }

    public static void addCookie(HttpServletResponse response, String name, String value, Integer expiresInSeconds) {
        Duration maxAge = safeDuration(expiresInSeconds);
        addCookie(response, name, value, maxAge);
    }


    public static void addCookie(HttpServletResponse response, String name, String value, Duration maxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(false)
                .sameSite("Strict")
                .path("/")
                .maxAge(maxAge)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
    private static Duration safeDuration(Integer expiresInSeconds) {
        if (expiresInSeconds == null || expiresInSeconds <= 0) {
            throw new IllegalArgumentException("만료 시간이 유효하지 않습니다.");
        }
        return Duration.ofSeconds(expiresInSeconds);
    }

    public static void deleteCookie(HttpServletResponse response, String name) {
        ResponseCookie cookie = ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Strict")
                .path("/")
                .maxAge(0) // 즉시 만료
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}