package com.mini2.user_service.domain.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoResponseDto {
    private String email;
    private String name;
}
