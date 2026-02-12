package it.bicocca.eduquest.security;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component 
public class JwtUtils {

    @Value("${eduquest.app.jwtSecret}")
    private String jwtSecret;

    @Value("${eduquest.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    // Token generation
    public String generateToken(long userId) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId)) 
                .setIssuedAt(new Date()) 
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) 
                .compact();
    }

    // Token validation
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false; 
        }
    }

    // Extraction ID from user
    public long getUserIdFromToken(String token) {
        String userIdStr = Jwts.parserBuilder()
                .setSigningKey(getSigningKey()) 
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        
        return Long.parseLong(userIdStr);
    }
}