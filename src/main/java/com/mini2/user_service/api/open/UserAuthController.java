package com.mini2.user_service.api.open;

import com.mini2.user_service.common.dto.ApiResponseDto;
import com.mini2.user_service.common.web.context.GatewayRequestHeaderUtils;
import com.mini2.user_service.domain.dto.UserLoginRequestDto;
import com.mini2.user_service.domain.dto.UserRegisterRequestDto;
import com.mini2.user_service.secret.jwt.dto.TokenDto;
import com.mini2.user_service.service.RefreshTokenService;
import com.mini2.user_service.service.UserAuthService;
import com.mini2.user_service.util.CookieUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequestMapping(value = "/api/user/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Auth API", description = "사용자 인증/인가 관련 API")
public class UserAuthController {
    private final UserAuthService userAuthService;
    private final RefreshTokenService refreshTokenService;

    @Operation(summary = "회원가입", description = "이름, 이메일, 비밀번호를 입력받아 회원가입을 진행합니다.")
    @PostMapping(value = "/auth/signup")
    public ApiResponseDto<TokenDto.AccessToken> register(@RequestBody @Valid UserRegisterRequestDto registerDto , HttpServletResponse response) {
        String deviceInfo = GatewayRequestHeaderUtils.getClientDeviceOrThrowException();
        TokenDto.AccessRefreshToken token = userAuthService.registerUser(registerDto , deviceInfo);
        CookieUtils.addCookie(response, "refreshToken", token.getRefreshToken().getToken(), token.getRefreshToken().getExpiresIn());
        return ApiResponseDto.createOk(new TokenDto.AccessToken(token.getAccessToken()));
    }

    @Operation(summary = "사용자 로그인", description = "이메일과 비밀번호를 입력받아 JWT 액세스/리프레시 토큰을 반환합니다.")
    @PostMapping(value = "/auth/login")
    public ApiResponseDto<TokenDto.AccessToken> login(@RequestBody @Valid UserLoginRequestDto loginDto, HttpServletResponse response , HttpServletRequest request) {
        String deviceInfo = GatewayRequestHeaderUtils.getClientDeviceOrThrowException();
        TokenDto.AccessRefreshToken token = userAuthService.login(loginDto , deviceInfo);
        CookieUtils.addCookie(response, "refreshToken", token.getRefreshToken().getToken(), token.getRefreshToken().getExpiresIn());
        return ApiResponseDto.createOk(new TokenDto.AccessToken(token.getAccessToken()));
    }

    @Operation(summary = "AccessToken 재발급", description = "만료된 AccessToken을 리프레시 토큰을 통해 재발급합니다.")
    @PostMapping("/token/refresh")
    public ApiResponseDto<TokenDto.AccessToken> refreshToken(HttpServletRequest request) {
        String refreshToken = CookieUtils.extractRefreshToken(request);
        String deviceInfo = GatewayRequestHeaderUtils.getClientDeviceOrThrowException();
        TokenDto.AccessToken accessToken = refreshTokenService.reissueAccessToken(refreshToken, deviceInfo);
        return ApiResponseDto.createOk(accessToken);
    }

    @Operation(summary = "로그아웃", description = "리프레시 토큰을 비활성화 하고 쿠키 삭제를 통해 로그아웃을 처리합니다.")
    @PostMapping("/logout")
    public ApiResponseDto<String> logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = CookieUtils.extractRefreshToken(request);
        String deviceInfo = GatewayRequestHeaderUtils.getClientDeviceOrThrowException();
        userAuthService.logout(refreshToken, deviceInfo);
        CookieUtils.deleteCookie(response,"refreshToken");
        return ApiResponseDto.createOk("로그아웃 되었습니다.");
    }

}
