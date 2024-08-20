package com.example.rolebasedauth.security.jwt;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

import com.example.rolebasedauth.service.MyUserDetails;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.jwtExp}")
    private int JWT_EXP;

    private SecretKey getSigningKey() {
        // Generate a secure key if the SECRET_KEY is not provided or is too short
        if (SECRET_KEY == null || SECRET_KEY.length() < 32) {
            logger.warn("The provided SECRET_KEY is not secure enough. Generating a secure key...");
            return Keys.secretKeyFor(SignatureAlgorithm.HS256); // generates a secure key
        }
        byte[] keyBytes = SECRET_KEY.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(MyUserDetails myUserDetails) {
        List<String> roles = myUserDetails.getAuthorities().stream().map(item -> item.getAuthority()).collect(Collectors.toList());
        Map<String, Object> claims = new HashMap<>();
        claims.put("ROLES", roles);

        return createToken(claims, myUserDetails.getUsername());
    }

    public String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXP))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
            logger.error("Invalid token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            logger.error("Expired token: {}", ex.getMessage());
        }

        return false;
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody().getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return (List<String>) extractClaim(token).get("ROLES");
    }

    public Claims extractClaim(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }
}