package com.kargobaji.kargobaji.openAPI.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ApiResponseDto<T> {
    private int status;
    private String message;
    private T data;

    public static <T> ApiResponseDto<T> success(T data) {
        return ApiResponseDto.<T>builder()
                .status(200)
                .message("success")
                .data(data)
                .build();
    }

    public static <T> ApiResponseDto<T> fail(int status, String message) {
        return ApiResponseDto.<T>builder()
                .status(status)
                .message(message)
                .data(null)
                .build();
    }
}
