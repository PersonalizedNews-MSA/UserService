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

    //토큰 저장
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


    //토큰 재발급
    public TokenDto.AccessToken reissueAccessToken(String refreshTokenValue, String deviceInfo) {
        //리프레시 토큰을 디코딩하여 userId 추출 (유효성 검증 포함)
        String userIdStr = tokenGenerator.validateJwtToken(refreshTokenValue);
        if (userIdStr == null) {
            // 토큰이 만료되었거나 서명 위조된 경우
            throw new BadParameter("유효하지 않은 토큰입니다.");
        }
        Long userId = Long.parseLong(userIdStr);

        //DB에서 해당 userId와 deviceInfo로 저장된 리프레시 토큰 엔티티 조회
        RefreshToken tokenEntity = refreshTokenRepository.findByUserIdAndDeviceInfo(userId, deviceInfo)
                .orElseThrow(() -> new NotFound("해당 리프레시 토큰을 찾을 수 없습니다."));

        //리프레시 토큰이 유효하지 않거나 만료되었는지 확인
        if (!tokenEntity.isValid() || tokenEntity.getExpiredAt().isBefore(LocalDateTime.now())) {
            // 만료되었거나 비활성화된 토큰은 무효화 처리
            tokenEntity.invalidate();
            throw new BadParameter("만료되었거나 유효하지 않은 리프레시 토큰입니다.");
        }

        // 모든 검증 통과 → 새로운 AccessToken 생성 후 반환
        return tokenGenerator.generateAccessToken(userId, deviceInfo);
    }


    // 로그아웃
    public void logout(Long userId, String deviceInfo) {
        RefreshToken token = refreshTokenRepository.findByUserIdAndDeviceInfo(userId, deviceInfo)
                .orElseThrow(() -> new NotFound("해당 사용자의 토큰이 없습니다."));

        token.invalidate();
    }

}