package pt.ua.tqs.mober.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pt.ua.tqs.mober.model.Role;
import pt.ua.tqs.mober.model.User;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    private final String secretKeyString;
    private final long expirationMs;

    public JwtService(
            @Value("${mober.jwt.secret:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}") String secretKey,
            @Value("${mober.jwt.expiration-ms:86400000}") long expirationMs
    ) {
        this.secretKeyString = secretKey;
        this.expirationMs = expirationMs;
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKeyString.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("name", user.getName());
        claims.put("role", user.getRole().name());

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .claims(claims)
                .subject(user.getEmail())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Role extractRole(String token) {
        String roleStr = extractAllClaims(token).get("role", String.class);
        return Role.valueOf(roleStr);
    }

    public boolean isTokenValid(String token, User user) {
        try {
            Claims claims = extractAllClaims(token);
            String email = claims.getSubject();
            boolean isExpired = claims.getExpiration().before(new Date());
            return email.equals(user.getEmail()) && !isExpired;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
