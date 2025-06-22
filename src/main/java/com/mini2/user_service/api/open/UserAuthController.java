package com.mini2.user_service.api.open;

import com.mini2.user_service.common.dto.ApiResponseDto;
import com.mini2.user_service.domain.dto.SiteUserRegisterDto;
import com.mini2.user_service.service.SiteUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

@Slf4j
@RestController
@RequestMapping(value = "/api/user/v1/auth", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Auth API", description = "사용자 인증/인가 관련 API")
public class UserAuthController {
    private final SiteUserService siteUserService;

    @Operation(summary = "회원가입", description = "이름, 이메일, 비밀번호를 입력받아 회원가입을 진행합니다.")
    @PostMapping(value = "/signup")
    public ApiResponseDto<String> register(@RequestBody @Valid SiteUserRegisterDto registerDto) {
        siteUserService.registerUser(registerDto);
        return ApiResponseDto.defaultOk();
    }

}
