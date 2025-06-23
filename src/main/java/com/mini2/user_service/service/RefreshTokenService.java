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
            RefreshToken existingToken = optional.get();
            existingToken.update(token.getToken(), expiredAt);
        } else {
            RefreshToken newToken = new RefreshToken(
                    null, userId, token.getToken(), expiredAt, true, deviceInfo, null, null
            );
            refreshTokenRepository.save(newToken);
        }
    }

    public void updateToken(Long userId, String newToken, LocalDateTime newExpiredAt) {
        refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFound("토큰 없음"));
//        token.update(newToken, newExpiredAt);
    }

    public RefreshToken getByToken(String token) {
        return refreshTokenRepository.findByRefreshToken(token)
                .orElseThrow(() -> new NotFound("해당 리프레시 토큰을 찾을 수 없습니다."));
    }


    public TokenDto.AccessToken reissueAccessToken(String refreshTokenValue, String deviceInfo) {
        RefreshToken tokenEntity = getByToken(refreshTokenValue);

        if (!tokenEntity.isValid() || tokenEntity.getExpiredAt().isBefore(LocalDateTime.now())) {
            tokenEntity.invalidate();
            throw new BadParameter("만료되었거나 유효하지 않은 리프레시 토큰입니다.");
        }

        TokenDto.AccessToken newAccessToken = tokenGenerator.generateAccessToken(tokenEntity.getUserId(), deviceInfo);
        return newAccessToken;
    }

}