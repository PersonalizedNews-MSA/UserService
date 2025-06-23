package com.mini2.user_service.service;

import com.mini2.user_service.common.exception.BadParameter;
import com.mini2.user_service.domain.User;
import com.mini2.user_service.domain.dto.SiteUserRegisterDto;
import com.mini2.user_service.domain.repository.UserRepository;
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
class UserAuthServiceTest {
    @Autowired
    private UserAuthService userAuthService;
    @Autowired
    private UserRepository userRepository;

    @Test
    void 회원가입_성공() {
        // given
        SiteUserRegisterDto dto = new SiteUserRegisterDto();
        dto.setEmail("test@example.com");
        dto.setPassword("Password123!");
        dto.setName("홍길동");

        // when
        userAuthService.registerUser(dto);

        // then
        Optional<User> user = userRepository.findByEmail("test@example.com");
        assertTrue(user.isPresent());
        assertEquals("홍길동", user.get().getName());
    }

    @Test
    void 이메일_중복_회원가입_실패() {
        // given
        User user = User.create("test@example.com", "Password123!", "홍길동");
        userRepository.save(user);

        SiteUserRegisterDto dto = new SiteUserRegisterDto();
        dto.setEmail("test@example.com");
        dto.setPassword("Password123!");
        dto.setName("홍길동");

        // when & then
        assertThrows(BadParameter.class, () -> userAuthService.registerUser(dto));
    }

    @Test
    void 이메일_대문자입력_저장시_소문자처리() {
        // given
        SiteUserRegisterDto dto = new SiteUserRegisterDto();
        dto.setEmail("TEST@EXAMPLE.COM");
        dto.setPassword("Password123!");
        dto.setName("홍길동");

        // when
        userAuthService.registerUser(dto);

        // then
        Optional<User> user = userRepository.findByEmail("test@example.com");
        assertTrue(user.isPresent());
        assertEquals("test@example.com", user.get().getEmail());
    }
}