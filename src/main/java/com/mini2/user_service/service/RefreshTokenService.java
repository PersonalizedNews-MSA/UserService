package com.mini2.user_service.service;

import com.mini2.user_service.common.exception.NotFound;
import com.mini2.user_service.domain.RefreshToken;
import com.mini2.user_service.domain.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public void saveToken(Long userId, String token, LocalDateTime expiredAt, String deviceInfo) {
        RefreshToken refreshToken = new RefreshToken(
                null, userId, token, expiredAt, true, deviceInfo, null, null
        );
        refreshTokenRepository.save(refreshToken);
    }

    public void updateToken(Long userId, String newToken, LocalDateTime newExpiredAt) {
        RefreshToken token = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFound("토큰 없음"));
        token.update(newToken, newExpiredAt);
    }

    public Optional<RefreshToken> getByToken(String token) {
        return refreshTokenRepository.findByRefreshToken(token);
    }

    public void invalidateToken(String token) {
        RefreshToken tokenEntity = refreshTokenRepository.findByRefreshToken(token)
                .orElseThrow(() -> new NotFound("토큰 없음"));
        tokenEntity.invalidate();
    }
}