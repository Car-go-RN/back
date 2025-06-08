package com.kargobaji.kargobaji.loginSignup.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final Map<String, VerificationCodeInfo> verificationCodes = new HashMap<>();

    public void sendVerificationCode(String email) {
        String code = generateCode();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(5); // 유효시간 5분 설정
        verificationCodes.put(email, new VerificationCodeInfo(code, expiresAt));

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("[카고바지] 이메일 인증코드입니다.");
        message.setText("인증번호: " + code);

        try {
            mailSender.send(message);
            System.out.println("이메일 전송 성공: " + email + " / 인증코드: " + code + " / 유효시간: " + expiresAt);
        } catch (Exception e) {
            System.out.println("이메일 전송 실패: " + email);
            e.printStackTrace();
        }
    }

    public boolean verifyCode(String email, String code) {
        VerificationCodeInfo info = verificationCodes.get(email);
        if (info == null) {
            System.out.println("인증 실패: 코드 없음");
            return false;
        }

        // 만료시간 체크
        if (LocalDateTime.now().isAfter(info.getExpiresAt())) {
            verificationCodes.remove(email); // 만료된 건 삭제
            System.out.println("인증 실패: 코드 만료됨");
            return false;
        }

        boolean result = code.equals(info.getCode());
        if (result) {
            verificationCodes.remove(email); // 성공하면 코드 삭제 (1회용 인증)
            System.out.println("인증 성공");
        } else {
            System.out.println("인증 실패: 코드 불일치");
        }
        return result;
    }

    private String generateCode() {
        return String.valueOf(new Random().nextInt(900000) + 100000); // 100000~999999
    }

    // 내부 클래스로 VerificationCodeInfo 정의
    private static class VerificationCodeInfo {
        private final String code;
        private final LocalDateTime expiresAt;

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
}
