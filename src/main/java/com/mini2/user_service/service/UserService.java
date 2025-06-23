package com.mini2.user_service.service;

import com.mini2.user_service.common.exception.BadParameter;
import com.mini2.user_service.common.exception.NotFound;
import com.mini2.user_service.domain.User;
import com.mini2.user_service.domain.dto.EmailCheckRequestDto;
import com.mini2.user_service.domain.dto.UserInfoResponseDto;
import com.mini2.user_service.domain.dto.UserUpdateRequestDto;
import com.mini2.user_service.domain.repository.UserRepository;
import com.mini2.user_service.secret.jwt.TokenGenerator;
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


    //이메일 중복확인
    public boolean isEmailAvailable(EmailCheckRequestDto emailDto) {
        String email = emailDto.getEmail().toLowerCase();
        return !userRepository.existsByEmailAndDeletedFalse(email);
    }

    //유저 정보 조회
    public UserInfoResponseDto getUserInfo(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFound("사용자 정보를 찾을 수 없습니다."));

        return UserInfoResponseDto.builder()
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }
    //유저 정보 수정
    public void updateUserInfo(Long userId,UserUpdateRequestDto userDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFound("사용자를 찾을 수 없습니다."));
        user.updateUsername(userDto.getName());
    }

    //회원 탈퇴
    public void withdrawByRequest(String refreshToken ,String deviceInfo) {
        String userIdStr = tokenGenerator.validateJwtToken(refreshToken);

        if (userIdStr == null) throw new BadParameter("잘못된 토큰입니다.");
        Long userId = Long.parseLong(userIdStr);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFound("유저를 찾을 수 없습니다."));

        user.markAsDeleted();
        refreshTokenService.logout(userId, deviceInfo);
    }
}
