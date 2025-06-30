package com.kargobaji.kargobaji.loginSignup.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret_key}")
    private String secretKey;

    @Value("${jwt.issuer}")
    private String issuer;

    // JWT 유효 시간: 24시간
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    // JWT 토큰 생성
    public String createToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuer(issuer)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // 토큰 검증 및 이메일 추출
    public String validateAndGetEmail(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    // JwtAuthenticationFilter에서 사용하기 위한 메서드들 ↓↓↓↓↓

    // 이메일 추출 (실제로는 validateAndGetEmail 위임)
    public String extractEmail(String token) {
        return validateAndGetEmail(token);
    }

    // 토큰 유효성 검증
    public boolean validateToken(String token, org.springframework.security.core.userdetails.UserDetails userDetails) {
        String email = extractEmail(token);
        return email.equals(userDetails.getUsername());
    }
}
