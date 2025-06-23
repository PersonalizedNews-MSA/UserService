package com.mini2.user_service.service;


import com.mini2.user_service.common.exception.BadParameter;
import com.mini2.user_service.common.exception.NotFound;
import com.mini2.user_service.domain.SiteUser;
import com.mini2.user_service.domain.dto.SiteUserLoginDto;
import com.mini2.user_service.domain.dto.SiteUserRegisterDto;
import com.mini2.user_service.domain.repository.SiteUserRepository;
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
public class SiteUserService {
    private final SiteUserRepository siteUserRepository;
    private final TokenGenerator tokenGenerator;

    //회원가입
    public void registerUser(SiteUserRegisterDto registerDto) {
        String email = registerDto.getEmail().toLowerCase();

        Optional<SiteUser> optionalUser = siteUserRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            throw new BadParameter("이미 사용 중인 이메일입니다. 다른 이메일을 사용해주세요.");
        }

        SiteUser siteUser = SiteUser.create(email, registerDto.getPassword(), registerDto.getName());
        siteUserRepository.save(siteUser);
    }

    // 로그인
    @Transactional(readOnly = true)
    public TokenDto.AccessRefreshToken login(SiteUserLoginDto loginDto) {
        String userId = loginDto.getEmail().toLowerCase();

        SiteUser user = siteUserRepository.findByEmail(userId)
                .orElseThrow(() -> new NotFound("아이디 또는 비밀번호를 확인하세요."));

        if( !SecureHashUtils.matches(loginDto.getPassword(), user.getPassword())){
            throw new BadParameter("아이디 또는 비밀번호를 확인하세요.");
        }
        return tokenGenerator.generateAccessRefreshToken(userId, "WEB");
    }


}