package com.kargobaji.kargobaji.loginSignup.controller;

import com.kargobaji.kargobaji.loginSignup.domain.User;
import com.kargobaji.kargobaji.loginSignup.dto.LoginRequestDto;
import com.kargobaji.kargobaji.loginSignup.dto.LoginResponseDto;
import com.kargobaji.kargobaji.loginSignup.dto.ResetPasswordRequest;
import com.kargobaji.kargobaji.loginSignup.jwt.JwtUtil;
import com.kargobaji.kargobaji.loginSignup.repository.UserRepository;
import com.kargobaji.kargobaji.loginSignup.service.PasswordResetService;
import com.kargobaji.kargobaji.loginSignup.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordResetService passwordResetService;
    private final UserRepository userRepository;

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                        .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));
        String email = user.getEmail();
        Long userId = user.getId();

        userService.authenticate(request.getEmail(), request.getPassword());
        String token = jwtUtil.createToken(request.getEmail());
        return ResponseEntity.ok(new LoginResponseDto(userId, token));
    }

    // 비밀번호 재설정 링크 전송
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        passwordResetService.sendResetLink(email);
        return ResponseEntity.ok("비밀번호 재설정 링크가 이메일로 전송되었습니다.");
    }

    // 비밀번호 재설정
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
    }
}