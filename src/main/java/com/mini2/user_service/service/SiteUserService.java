package com.mini2.user_service.service;


import com.mini2.user_service.common.exception.BadParameter;
import com.mini2.user_service.domain.SiteUser;
import com.mini2.user_service.domain.dto.SiteUserRegisterDto;
import com.mini2.user_service.domain.repository.SiteUserRepository;
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


}