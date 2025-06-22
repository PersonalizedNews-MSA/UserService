package com.mini2.user_service.service;

import com.mini2.user_service.common.exception.BadParameter;
import com.mini2.user_service.domain.SiteUser;
import com.mini2.user_service.domain.dto.SiteUserRegisterDto;
import com.mini2.user_service.domain.repository.SiteUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class SiteUserServiceTest {
    @Autowired
    private SiteUserService siteUserService;
    @Autowired
    private SiteUserRepository siteUserRepository;

    @Test
    void 회원가입_성공() {
        // given
        SiteUserRegisterDto dto = new SiteUserRegisterDto();
        dto.setEmail("test@example.com");
        dto.setPassword("Password123!");
        dto.setName("홍길동");

        // when
        siteUserService.registerUser(dto);

        // then
        Optional<SiteUser> user = siteUserRepository.findByEmail("test@example.com");
        assertTrue(user.isPresent());
        assertEquals("홍길동", user.get().getName());
    }

    @Test
    void 이메일_중복_회원가입_실패() {
        // given
        SiteUser siteUser = SiteUser.create("test@example.com", "Password123!", "홍길동");
        siteUserRepository.save(siteUser);

        SiteUserRegisterDto dto = new SiteUserRegisterDto();
        dto.setEmail("test@example.com");
        dto.setPassword("Password123!");
        dto.setName("홍길동");

        // when & then
        assertThrows(BadParameter.class, () -> siteUserService.registerUser(dto));
    }

    @Test
    void 이메일_대문자입력_저장시_소문자처리() {
        // given
        SiteUserRegisterDto dto = new SiteUserRegisterDto();
        dto.setEmail("TEST@EXAMPLE.COM");
        dto.setPassword("Password123!");
        dto.setName("홍길동");

        // when
        siteUserService.registerUser(dto);

        // then
        Optional<SiteUser> user = siteUserRepository.findByEmail("test@example.com");
        assertTrue(user.isPresent());
        assertEquals("test@example.com", user.get().getEmail());
    }
}