package com.mini2.user_service.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedDate;

import jakarta.persistence.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Slf4j
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "refresh_token")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RefreshToken {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String refreshToken;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    @Column(nullable = false)
    private boolean valid = true;

    @Column(length = 100)
    private String deviceInfo;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public void update(String newToken, LocalDateTime newExpiredAt) {
        this.refreshToken = newToken;
        this.expiredAt = newExpiredAt;
        this.valid = true;
    }

    public void markAsValid() {
        this.valid = true;
    }

    public void invalidate() {
        this.valid = false;
    }

    public static RefreshToken create(Long userId, String token, LocalDateTime expiredAt, String deviceInfo) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.userId = userId;
        refreshToken.refreshToken = token;
        refreshToken.expiredAt = expiredAt;
        refreshToken.deviceInfo = deviceInfo;
        refreshToken.valid = true;
        return refreshToken;
    }


}
