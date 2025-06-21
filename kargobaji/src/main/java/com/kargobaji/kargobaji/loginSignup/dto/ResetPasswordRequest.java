package com.kargobaji.kargobaji.loginSignup.dto;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String token;
    private String newPassword;
}
