package com.example.rolebasedauth.security.jwt;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import com.example.rolebasedauth.model.RefreshToken;
import com.example.rolebasedauth.repository.RefreshTokenRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.rolebasedauth.repository.UserRepository;

import javax.crypto.SecretKey;

@Service
public class RefreshTokenService {

    @Value("${jwt.refreshExp}")
    private Long refreshTokenDurationMs;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    @Value("${jwt.jwtExp}")
    private int JWT_EXP;

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    public RefreshToken findByRefreshToken(String token) {
        return refreshTokenRepository.findByRefreshToken(token);
    }

    public RefreshToken findByUserId(Long userId) {
        return refreshTokenRepository.findByUserId(userId);
    }

    public RefreshToken createRefreshToken(Long userId) {
        RefreshToken existingToken = findByUserId(userId);

        if (existingToken != null) {
            // Update the expiration date of the existing token if necessary
            existingToken.setExpDate(Instant.now().plusMillis(refreshTokenDurationMs));
            return refreshTokenRepository.save(existingToken);
        }

        // Create a new token if none exists
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found")));
        String token = generateRefreshToken(userId);
        refreshToken.setRefreshToken(token);
        refreshToken.setExpDate(Instant.now().plusMillis(refreshTokenDurationMs));
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            return null;
        }

        return token;
    }

    @Transactional
    public int deleteByUserId(Long userId) {
        return refreshTokenRepository.deleteByUser(userRepository.findById(userId).get());
    }

    private SecretKey getSigningKey() {
        // Generate a secure key if the SECRET_KEY is not provided or is too short
        if (SECRET_KEY == null || SECRET_KEY.length() < 32) {
            logger.warn("The provided SECRET_KEY is not secure enough. Generating a secure key...");
            return Keys.secretKeyFor(SignatureAlgorithm.HS256); // generates a secure key
        }
        byte[] keyBytes = SECRET_KEY.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String generateRefreshToken(Long userId) {
        // Generate a random UUID to add length to the token
        String randomData = UUID.randomUUID().toString();

        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenDurationMs))
                .claim("randomData", randomData) // Adding random data as an additional claim
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}
