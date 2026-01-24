package it.bicocca.eduquest.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

//for Spring to server
@Component 
public class JwtUtils {

    // Long secret key >32 ch
    private static final String SECRET = "EduQuest_Secret_Key_Bicocca_Super_Sicura_123!";
    
    // Token expire time
    private static final long EXPIRATION_TIME = 86400000; 

    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    // Token generation
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email) // Salviamo l'email dentro il token
                .setIssuedAt(new Date()) // Data creazione
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Data scadenza
                .signWith(key, SignatureAlgorithm.HS256) // Firma con la chiave segreta
                .compact();
    }

    // Token validation
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false; // Token non valido
        }
    }

    // Extra method for getting user email from token
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}