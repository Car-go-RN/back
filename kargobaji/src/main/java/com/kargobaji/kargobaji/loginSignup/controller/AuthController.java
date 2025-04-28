package com.kargobaji.kargobaji.loginSignup.controller;

import com.kargobaji.kargobaji.loginSignup.dto.LoginRequestDto;
import com.kargobaji.kargobaji.loginSignup.dto.LoginResponseDto;
import com.kargobaji.kargobaji.loginSignup.jwt.JwtUtil;
import com.kargobaji.kargobaji.loginSignup.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
        userService.authenticate(request.getUsername(), request.getPassword());
        String token = jwtUtil.createToken(request.getUsername());
        return ResponseEntity.ok(new LoginResponseDto(token));
    }
}
