package com.mini2.user_service.secret.jwt.util;

import jakarta.servlet.http.HttpServletRequest;

public class DeviceUtils {

    public static String getDeviceInfo(HttpServletRequest request) {
        return request != null
                ? request.getHeader("User-Agent") != null
                ? request.getHeader("User-Agent")
                : "UNKNOWN"
                : "UNKNOWN";
    }
}