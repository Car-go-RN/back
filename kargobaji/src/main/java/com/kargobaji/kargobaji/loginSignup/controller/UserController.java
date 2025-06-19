package com.kargobaji.kargobaji.loginSignup.controller;

import com.kargobaji.kargobaji.loginSignup.domain.User;
import com.kargobaji.kargobaji.loginSignup.dto.UserRegisterRequestDto;
import com.kargobaji.kargobaji.loginSignup.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegisterRequestDto request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword()); // UserService.register() 안에서 encode 처리됨.

        userService.register(user);
        return ResponseEntity.ok("회원가입 성공!");
    }
}

