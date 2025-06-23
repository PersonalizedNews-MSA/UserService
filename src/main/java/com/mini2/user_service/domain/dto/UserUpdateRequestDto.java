package com.mini2.user_service.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserUpdateRequestDto {
    @NotBlank(message = "이름을 입력하세요.")
    private String name;
}
