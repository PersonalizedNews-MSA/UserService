package com.mini2.user_service.service;

import com.mini2.user_service.common.exception.BadParameter;
import com.mini2.user_service.common.exception.NotFound;
import com.mini2.user_service.domain.RefreshToken;
import com.mini2.user_service.domain.repository.RefreshTokenRepository;
import com.mini2.user_service.secret.jwt.TokenGenerator;
import com.mini2.user_service.secret.jwt.dto.TokenDto;
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
    private final TokenGenerator tokenGenerator;

    public void saveToken(Long userId, TokenDto.JwtToken token, String deviceInfo) {
        LocalDateTime expiredAt = LocalDateTime.now().plusSeconds(token.getExpiresIn());
        Optional<RefreshToken> optional = refreshTokenRepository.findByUserIdAndDeviceInfo(userId, deviceInfo);

        if (optional.isPresent()) {
            RefreshToken existing = optional.get();

            // 만료 여부 확인
            if (existing.getExpiredAt().isAfter(LocalDateTime.now())) {
                // 아직 유효한 경우 (로그아웃한 사용자일 수도 있음)
                existing.update(token.getToken(), expiredAt);
                if (!existing.isValid()) {
                    existing.markAsValid();
                }
            } else {
                // 만료된 경우에도 갱신
                existing.update(token.getToken(), expiredAt);
                existing.markAsValid();
            }

        } else {
            RefreshToken newToken = RefreshToken.create(userId, token.getToken(), expiredAt, deviceInfo);
            refreshTokenRepository.save(newToken);
        }
    }


    public TokenDto.AccessToken reissueAccessToken(String refreshTokenValue, String deviceInfo) {
        String userIdStr = tokenGenerator.validateJwtToken(refreshTokenValue);
        if (userIdStr == null) {
            throw new BadParameter("유효하지 않은 토큰입니다.");
        }
        Long userId = Long.parseLong(userIdStr);

        RefreshToken tokenEntity = refreshTokenRepository.findByUserIdAndDeviceInfo(userId, deviceInfo)
                .orElseThrow(() -> new NotFound("해당 리프레시 토큰을 찾을 수 없습니다."));

        if (!tokenEntity.isValid() || tokenEntity.getExpiredAt().isBefore(LocalDateTime.now())) {
            tokenEntity.invalidate();
            throw new BadParameter("만료되었거나 유효하지 않은 리프레시 토큰입니다.");
        }

        return tokenGenerator.generateAccessToken(userId, deviceInfo);
    }


    // 로그아웃
    public void logout(Long userId, String deviceInfo) {
        RefreshToken token = refreshTokenRepository.findByUserIdAndDeviceInfo(userId, deviceInfo)
                .orElseThrow(() -> new NotFound("해당 사용자의 토큰이 없습니다."));

        token.invalidate();
    }

}