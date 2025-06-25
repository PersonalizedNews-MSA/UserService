package com.mini2.user_service.service;

import com.mini2.user_service.common.exception.BadParameter;
import com.mini2.user_service.common.exception.NotFound;
import com.mini2.user_service.domain.User;
import com.mini2.user_service.domain.dto.UserLoginRequestDto;
import com.mini2.user_service.domain.dto.UserRegisterRequestDto;
import com.mini2.user_service.domain.repository.UserRepository;
import com.mini2.user_service.secret.hash.SecureHashUtils;
import com.mini2.user_service.secret.jwt.TokenGenerator;
import com.mini2.user_service.secret.jwt.dto.TokenDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserAuthService {
    private final UserRepository userRepository;
    private final TokenGenerator tokenGenerator;
    private final RefreshTokenService refreshTokenService;

    //회원가입
    public TokenDto.AccessRefreshToken registerUser(UserRegisterRequestDto registerDto ,String deviceInfo ) {
        String email = registerDto.getEmail().toLowerCase();

        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent() && !optionalUser.get().getDeleted()) {
            throw new BadParameter("이미 사용 중인 이메일입니다. 다른 이메일을 사용해주세요.");
        }

        User user = User.create(email, registerDto.getPassword(), registerDto.getName());
        userRepository.save(user);

        TokenDto.AccessRefreshToken token = tokenGenerator.generateAccessRefreshToken(user.getId(), "WEB");
        refreshTokenService.saveToken(user.getId(), token.getRefreshToken() , deviceInfo);
        return token;

    }

    // 로그인
    public TokenDto.AccessRefreshToken login(UserLoginRequestDto loginDto, String deviceInfo) {
        String email = loginDto.getEmail().toLowerCase();

        User user = userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new NotFound("아이디 또는 비밀번호를 확인하세요."));

        if( !SecureHashUtils.matches(loginDto.getPassword(), user.getPassword())){
            throw new BadParameter("아이디 또는 비밀번호를 확인하세요.");
        }
        TokenDto.AccessRefreshToken token = tokenGenerator.generateAccessRefreshToken(user.getId(), "WEB");
        refreshTokenService.saveToken(user.getId(), token.getRefreshToken() , deviceInfo);
        return token;
    }

    //로그아웃
    public void logout(String refreshToken, String deviceInfo) {
        String userIdStr = tokenGenerator.validateJwtToken(refreshToken);
        if (userIdStr == null) {
            throw new BadParameter("유효하지 않은 토큰입니다.");
        }
        Long userId = Long.parseLong(userIdStr);

        refreshTokenService.logout(userId, deviceInfo);
    }


}