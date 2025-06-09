package com.kargobaji.kargobaji.loginSignup.controller;

import com.kargobaji.kargobaji.loginSignup.dto.EmailRequest;
import com.kargobaji.kargobaji.loginSignup.dto.VerifyCodeRequest;
import com.kargobaji.kargobaji.loginSignup.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/email")
public class EmailVerificationController {

    private final EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<String> sendVerificationCode(@RequestBody EmailRequest request) {
        emailService.sendVerificationCode(request.getEmail());
        return ResponseEntity.ok("인증번호 전송 완료!");
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyCode(@RequestBody VerifyCodeRequest request) {
        boolean result = emailService.verifyCode(request.getEmail(), request.getCode());
        return result ?
                ResponseEntity.ok("인증 성공!") :
                ResponseEntity.status(400).body("인증 실패: 코드가 일치하지 않습니다.");
    }
}
