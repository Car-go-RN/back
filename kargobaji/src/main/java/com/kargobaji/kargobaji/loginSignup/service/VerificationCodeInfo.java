package com.kargobaji.kargobaji.loginSignup.service;

import java.time.LocalDateTime;

public class VerificationCodeInfo {
    private String code;
    private LocalDateTime expiresAt;

    public VerificationCodeInfo(String code, LocalDateTime expiresAt) {
        this.code = code;
        this.expiresAt = expiresAt;
    }

    public String getCode() {
        return code;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
}

