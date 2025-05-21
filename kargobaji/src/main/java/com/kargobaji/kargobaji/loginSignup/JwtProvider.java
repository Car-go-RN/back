package com.kargobaji.kargobaji.loginSignup.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtProvider {

    private final String secretKey;
    private final String issuer;
    private final long expiration;

    public JwtProvider(@Value("${jwt.secret_key}") String secretKey,
                       @Value("${jwt.issuer}") String issuer,
                       @Value("${jwt.expiration}") long expiration) {
        this.secretKey = secretKey;
        this.issuer = issuer;
        this.expiration = expiration;
    }

    public String generateToken(String username) {
        long now = System.currentTimeMillis();
        Date expiryDate = new Date(now + expiration);

        return Jwts.builder()
                .setSubject(username)
                .setIssuer(issuer)
                .setIssuedAt(new Date(now))
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes())
                .compact();
    }

    public String validateAndGetUsername(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey.getBytes())
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
