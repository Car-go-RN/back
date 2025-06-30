package com.kargobaji.kargobaji.loginSignup.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {
    private String currentPassword;
    private String newPassword;
}
