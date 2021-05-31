package com.test.teamlog.security;

import com.test.teamlog.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {
    public static final long TOKEN_VALIDATION_SECOND = 1000L * 60 * 60 * 24 * 7 * 3;

    @Value("${app.jwtSecret}")
    private String JWT_SECRET;

    // 유효한 토큰인지 검사, payload값 로드
    public Claims extractAllClaims(String token) throws ExpiredJwtException {
        return Jwts.parser()
                .setSigningKey(JWT_SECRET)
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateToken(User user) {
        return doGenerateToken(user.getId(), TOKEN_VALIDATION_SECOND);
    }

    // 토큰 생성, payload
    public String doGenerateToken(String userId, long expireTime) {
        Claims claims = Jwts.claims();
        claims.put("userId", userId);
        String jwt = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                .compact();
        return jwt;
    }

    public Boolean isTokenExpired(String token) {
        final Date expiration = extractAllClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    public String getUserId(String token) {
        return extractAllClaims(token).get("userId", String.class);
    }

    // 토큰 유효성 검사
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String userId = getUserId(token);
        return (userId.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
