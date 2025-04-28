//package com.kargobaji.kargobaji.loginSignup;
//import static com.cheering.auth.constant.JwtConstant.ACCESS_TOKEN_EXPIRE_TIME;
//import static com.cheering.auth.constant.JwtConstant.GRANT_TYPE;
//import static com.cheering.auth.constant.JwtConstant.REFRESH_TOKEN_EXPIRE_TIME;
//
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.io.Decoders;
//import io.jsonwebtoken.security.Keys;
//import java.security.Key;
//import java.util.Date;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//@Component
//public class JwtGenerator {
//    private final Key key;
//
//    public JwtGenerator(@Value(("${jwt.secret}") String secretKey) {
//        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
//        this.key = keys.hmacShaKeyFor(keyBytes);
//    }
//
//    public JWToken generateToken(Long userId) {
//        long now = (new Date()).getTime();
//
//        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
//
//        String accessToken = Jwts.builder()
//                .setSubject(String.valueOf(userId))
//                .claim("auth", authorities)
//                .setExpiration(accessTokenExpiresIn)
//                .signWith(key, SignatureAlgorithm.HS256)
//                .compact();
//
//        String refreshToken = Jwts.builder()
//                .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRE_TIME))
//                .signWith(key, SignatureAlgorithm.HS256)
//                .compact();
//
//        return JWToken.builder()
//                .grantType(GRANT_TYPE)
//                .accessToken(accessToken)
//                .refreshToken(refreshToken)
//                .build();
//    }
//
//}
