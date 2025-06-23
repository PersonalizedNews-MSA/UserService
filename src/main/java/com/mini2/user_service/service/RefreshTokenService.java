package com.mini2.user_service.service;

import com.mini2.user_service.common.exception.NotFound;
import com.mini2.user_service.domain.RefreshToken;
import com.mini2.user_service.domain.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public void saveToken(Long userId, String token, LocalDateTime expiredAt, String deviceInfo) {
        Optional<RefreshToken> optional = refreshTokenRepository.findByUserIdAndDeviceInfo(userId, deviceInfo);

        if (optional.isPresent()) {
            RefreshToken existingToken = optional.get();
            existingToken.update(token, expiredAt);
        } else {
            RefreshToken newToken = new RefreshToken(
                    null, userId, token, expiredAt, true, deviceInfo, null, null
            );
            refreshTokenRepository.save(newToken);
        }
    }

    public void updateToken(Long userId, String newToken, LocalDateTime newExpiredAt) {
        refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFound("토큰 없음"));
//        token.update(newToken, newExpiredAt);
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