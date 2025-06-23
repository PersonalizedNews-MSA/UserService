package com.mini2.user_service.service;

import com.mini2.user_service.common.exception.BadParameter;
import com.mini2.user_service.common.exception.NotFound;
import com.mini2.user_service.domain.User;
import com.mini2.user_service.domain.repository.UserRepository;
import com.mini2.user_service.secret.jwt.TokenGenerator;
import com.mini2.user_service.secret.jwt.util.DeviceUtils;
import com.mini2.user_service.util.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final TokenGenerator tokenGenerator;

    public void withdrawByRequest(HttpServletRequest request) {
        String refreshToken = CookieUtils.extractRefreshToken(request);
        String deviceInfo = DeviceUtils.getDeviceInfo(request);
        String userIdStr = tokenGenerator.validateJwtToken(refreshToken);

        if (userIdStr == null) throw new BadParameter("잘못된 토큰입니다.");
        Long userId = Long.parseLong(userIdStr);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFound("유저를 찾을 수 없습니다."));

        user.markAsDeleted();
        refreshTokenService.logout(userId, deviceInfo);
    }
}
