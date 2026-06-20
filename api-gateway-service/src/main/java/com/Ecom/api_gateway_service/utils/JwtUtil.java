package com.Ecom.api_gateway_service.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;

@Component
public class JwtUtil {
    private final String SECRET_KEY = "TaK+HaV^uvCHEFsEVfypW#7g9^k*Z8$V";
    private final long REFRESHTOKEN_EXPIRATION = 7 * 24 * 60 * 60 * 100;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public List<String> extractRoles(String token)
    {
        List<String> role= extractAllClaims(token).get("role",List.class);
        return role;
    }
    public Long extractUserId(String token)
    {
        return extractAllClaims(token).get("userId",Long.class);
    }
    public Date extractExpiration(String token) {

        return extractAllClaims(token).getExpiration();
    }

    public String extractToken(String token)
    {
        return extractAllClaims(token).getId();
    }

    private Claims extractAllClaims(String token) {
        System.out.println("Request comes in extractAllClaims");
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
    }

    private Boolean isTokenExpired(String token) {
System.out.println("request in isTokenExpired");
        return extractExpiration(token).before(new Date());
    }


    public Boolean validateToken(String token) {
        System.out.println("Request comes here");
        return !isTokenExpired(token);
    }
}
