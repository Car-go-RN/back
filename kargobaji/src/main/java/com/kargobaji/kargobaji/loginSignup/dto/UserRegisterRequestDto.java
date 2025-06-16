package com.kargobaji.kargobaji.loginSignup.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterRequestDto {
    private String email;
    private String password;
}
