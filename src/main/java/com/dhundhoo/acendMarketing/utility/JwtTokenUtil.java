package com.dhundhoo.acendMarketing.utility;

import com.dhundhoo.acendMarketing.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Component
public class JwtTokenUtil {
    private static final long ACCESS_TOKEN_EXPIRATION = 864000000;
    private static final String SECRET_KEY = "accenagencywebsitefordigitalmarkettingbydhundhooanddhundhooorganization";
    private static Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }


    public String generateAccessToken(String userId) {
        return Jwts.builder()
                .claim("userId", userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }


    // Method to generate a session code using userId and userRole
    public String generateSessionToken(String userId, String userRole, String accessCode) {
        return Jwts.builder()
                .claim("userId", userId)
                .claim("userRole", userRole)
                .claim("accessCode", accessCode)
                .setIssuedAt(new Date())
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    // Parse and verify the JWT token and extract claims
    public Claims parseToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException e) {
            throw new RuntimeException("Invalid session token.");
        }
    }
    public String extractUserIdFromSession(String sessionToken) {
        try {
            Claims claims = parseToken(sessionToken);
            return claims.get("userId", String.class);
        } catch (Exception e) {
            return null;
        }
    }

    public  String extractUserRole(String sessionToken) {
        try {
            Claims claims = parseToken(sessionToken);
            return claims.get("userRole", String.class);
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Invalid session token.");
        }
    }




}
