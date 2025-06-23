package com.mini2.user_service.api.open;

import com.mini2.user_service.common.dto.ApiResponseDto;
import com.mini2.user_service.domain.dto.EmailCheckRequestDto;
import com.mini2.user_service.domain.dto.SiteUserLoginDto;
import com.mini2.user_service.domain.dto.SiteUserRegisterDto;
import com.mini2.user_service.secret.jwt.dto.TokenDto;
import com.mini2.user_service.service.RefreshTokenService;
import com.mini2.user_service.service.SiteUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;

import java.time.Duration;

@Slf4j
@RestController
@RequestMapping(value = "/api/user/v1", produces = MediaType.APPLICATION_JSON_VALUE)
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

    @Operation(summary = "이메일 중복 확인", description = "이메일이 이미 존재하는지 확인합니다.")
    @PostMapping("/email")
    public ApiResponseDto<Boolean> checkEmail(@RequestBody @Valid EmailCheckRequestDto emailCheckDto){
        boolean isAvailable = siteUserService.isEmailAvailable(emailCheckDto);
        return ApiResponseDto.createOk(isAvailable);
    }

    @Operation(summary = "사용자 로그인", description = "이메일과 비밀번호를 입력받아 JWT 액세스/리프레시 토큰을 반환합니다.")
    @PostMapping(value = "/login")
    public ApiResponseDto<TokenDto.AccessRefreshToken> login(@RequestBody @Valid SiteUserLoginDto loginDto, HttpServletResponse response , HttpServletRequest request) {
        TokenDto.AccessRefreshToken token = siteUserService.login(loginDto,request);

        //HttpOnly 쿠키로 설정
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", token.getRefresh().getToken())
                .httpOnly(true)
                .secure(false)
                .sameSite("Strict")
                .path("/")
                .maxAge(Duration.ofDays(7))
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        return ApiResponseDto.createOk(token);
    }

}
