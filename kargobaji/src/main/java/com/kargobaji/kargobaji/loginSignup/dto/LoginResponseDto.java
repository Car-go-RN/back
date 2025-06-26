package com.kargobaji.kargobaji.loginSignup.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponseDto {
    private Long userId;
    private String email;
    private String token;
}
