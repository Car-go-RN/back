package com.kargobaji.kargobaji.loginSignup.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final Map<String, String> verificationCodes = new HashMap<>();

    public void sendVerificationCode(String email) {
        String code = generateCode();
        verificationCodes.put(email, code);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("[카고바지] 이메일 인증코드입니다.");
        message.setText("인증번호: " + code);

        try {
            mailSender.send(message);
            System.out.println("이메일 전송 성공: " + email + " / 인증코드: " + code);
        } catch (Exception e) {
            System.out.println("이메일 전송 실패: " + email);
            e.printStackTrace();
        }
    }

    public boolean verifyCode(String email, String code) {
        return code.equals(verificationCodes.get(email));
    }

    private String generateCode() {
        return String.valueOf(new Random().nextInt(900000) + 100000); // 100000~999999
    }
}
