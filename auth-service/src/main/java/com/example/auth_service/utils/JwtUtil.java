package com.example.auth_service.utils;

import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.*;

@Component
public class JwtUtil {
    private String SECRET_KEY = "TaK+HaV^uvCHEFsEVfypW#7g9^k*Z8$V";
    private final long REFRESHTOKEN_EXPIRATION = 7 * 24 * 60 * 60 * 100;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String extractUsername(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    public String extractToken(String token)
    {
        return extractAllClaims(token).getId();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
    }
    public List<String> extractRoles(String token)
    {
        List<String> role= extractAllClaims(token).get("role",List.class);
        return role;
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(Long userId,String username, List<String> roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role",roles);
        claims.put("userId",userId);
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().claims(claims).subject(subject).header().empty().add("typ", "JWT").and().issuedAt(new Date(System.currentTimeMillis())).expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 5 minutes expiration time
                .signWith(getSigningKey()).compact();
    }

    public String createRefreshToken(String userName) {
        return Jwts.builder().id(UUID.randomUUID().toString()).subject(userName).claim("type", "refreshtoken").header().empty().add("typ", "JWT").and().issuedAt(new Date(System.currentTimeMillis())).expiration(new Date(System.currentTimeMillis()+REFRESHTOKEN_EXPIRATION))
                .signWith(getSigningKey()).compact();

    }

    public String extractType(String token)
    {
        return extractAllClaims(token).get("type",String.class);
    }


    public Boolean validateToken(String token) {
        return !isTokenExpired(token);
    }
}
