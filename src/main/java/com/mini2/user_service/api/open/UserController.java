package com.mini2.user_service.api.open;


import com.mini2.user_service.common.dto.ApiResponseDto;
import com.mini2.user_service.secret.jwt.TokenGenerator;
import com.mini2.user_service.service.RefreshTokenService;
import com.mini2.user_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "User API", description = "사용자 정보 관련 API")
public class UserController {
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final TokenGenerator tokenGenerator;

    @Operation(summary = "회원 탈퇴", description = "현재 로그인된 사용자의 계정을 탈퇴 처리합니다.")
    @DeleteMapping("/signout")
    public ApiResponseDto<String> withdraw(HttpServletRequest request) {
        userService.withdrawByRequest(request);
        return ApiResponseDto.createOk("탈퇴 되었습니다.");
    }
}
