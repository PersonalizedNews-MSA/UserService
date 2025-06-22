package com.mini2.user_service.common.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@NoArgsConstructor
@Getter
public class ApiResponseDto<T> {
    private String code;
    private String message;
    private T data;

    private ApiResponseDto(String code, String message) {
        this.code = code;
        this.message = message;
    }

    private ApiResponseDto(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // 데이터가 있는경우
    public static <T> ApiResponseDto<T> createOk(T data) {

        return new ApiResponseDto<>("OK", "요청이 성공하였습니다.", data);
    }
    //데이터가 없는경우
    public static ApiResponseDto<String> defaultOk() {

        return ApiResponseDto.createOk(null);
    }
    //에러 처리
    public static ApiResponseDto<String> createError(String code, String message) {
        return new ApiResponseDto<>(code, message);
    }

    public static <T> ApiResponseDto<T> createError(String code, String message, T data) {
        return new ApiResponseDto<>(code, message, data);
    }
}
