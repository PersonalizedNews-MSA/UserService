package com.mini2.user_service.api.open;

import com.mini2.user_service.common.dto.ApiResponseDto;
import com.mini2.user_service.domain.dto.EmailCheckRequestDto;
import com.mini2.user_service.service.UserService;
import com.mini2.user_service.util.CookieUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "User API", description = "사용자 정보 관련 API")
public class UserController {
    private final UserService userService;

    @Operation(summary = "이메일 중복 확인", description = "이메일이 이미 존재하는지 확인합니다.")
    @PostMapping("/email-check")
    public ApiResponseDto<Boolean> checkEmail(@RequestBody @Valid EmailCheckRequestDto emailCheckDto){
        boolean isAvailable = userService.isEmailAvailable(emailCheckDto);
        return ApiResponseDto.createOk(isAvailable);
    }

    @Operation(summary = "회원 탈퇴", description = "현재 로그인된 사용자의 계정을 탈퇴 처리합니다.")
    @DeleteMapping("/signout")
    public ApiResponseDto<String> withdraw(HttpServletRequest request, HttpServletResponse response) {
        CookieUtils.deleteCookie(response,"refreshToken");
        userService.withdrawByRequest(request);
        return ApiResponseDto.createOk("탈퇴 되었습니다.");
    }
}
