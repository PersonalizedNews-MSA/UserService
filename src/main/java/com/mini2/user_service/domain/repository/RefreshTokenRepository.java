package com.mini2.user_service.domain.repository;

import com.mini2.user_service.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByRefreshToken(String token);
    Optional<RefreshToken> findByUserId(Long userId);
    Optional<RefreshToken> findByUserIdAndDeviceInfo(Long userId,String deviceInfo);
}
