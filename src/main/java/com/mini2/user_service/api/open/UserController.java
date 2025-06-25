package com.mini2.user_service.api.open;

import com.mini2.user_service.common.dto.ApiResponseDto;
import com.mini2.user_service.common.web.context.GatewayRequestHeaderUtils;
import com.mini2.user_service.domain.dto.EmailCheckRequestDto;
import com.mini2.user_service.domain.dto.UserInfoResponseDto;
import com.mini2.user_service.domain.dto.UserUpdateRequestDto;
import com.mini2.user_service.service.UserService;
import com.mini2.user_service.util.CookieUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/user/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "User API", description = "사용자 정보 관련 API")
public class UserController {
    private final UserService userService;

    @Operation(summary = "이메일 중복 확인", description = "이메일이 이미 존재하는지 확인합니다.")
    @PostMapping("/auth/email-check")
    public ApiResponseDto<Boolean> checkEmail(@RequestBody @Valid EmailCheckRequestDto emailCheckDto){
        boolean isAvailable = userService.isEmailAvailable(emailCheckDto);
        return ApiResponseDto.createOk(isAvailable);
    }

    @Operation(summary = "사용자 정보 조회", description = "사용자의 ID를 기반으로 사용자 정보를 조회합니다.")
    @GetMapping("/profile")
    public ApiResponseDto<UserInfoResponseDto> getMyInfo() {
        Long userId = Long.valueOf(GatewayRequestHeaderUtils.getUserIdOrThrowException());
        UserInfoResponseDto userInfo = userService.getUserInfo(userId);
        return ApiResponseDto.createOk(userInfo);
    }

    @Operation(summary = "유저 이름 수정", description = "현재 로그인된 사용자의 정보를 수정합니다.")
    @PutMapping("/name")
    public ApiResponseDto<String> updateUser( @RequestBody @Valid UserUpdateRequestDto userDto){
        Long userId = Long.valueOf(GatewayRequestHeaderUtils.getUserIdOrThrowException());
        userService.updateUserInfo(userId, userDto);
        return ApiResponseDto.createOk("사용자 정보 수정이 완료되었습니다.");
    }


    @Operation(summary = "회원 탈퇴", description = "현재 로그인된 사용자의 계정을 탈퇴 처리합니다.")
    @DeleteMapping("/signout")
    public ApiResponseDto<String> withdraw(HttpServletRequest request, HttpServletResponse response) {
        Long userId = Long.valueOf(GatewayRequestHeaderUtils.getUserIdOrThrowException());
        userService.withdrawByRequest(userId);
        CookieUtils.deleteCookie(response,"refreshToken");
        return ApiResponseDto.createOk("탈퇴 되었습니다.");
    }
}
