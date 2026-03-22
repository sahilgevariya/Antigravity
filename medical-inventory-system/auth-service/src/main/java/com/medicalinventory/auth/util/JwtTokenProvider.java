package com.medicalinventory.auth.util;

import com.medicalinventory.auth.config.JwtProperties;
import com.medicalinventory.auth.domain.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * JWT utility for generating and validating tokens.
 *
 * <p>Uses HMAC-SHA256 for signing. In production, this can be swapped to RS256
 * by loading RSA keys and changing the signing/validation methods.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

    /**
     * Generate an access token for the given user.
     */
    public String generateAccessToken(User user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtProperties.getExpirationMs());

        Set<String> roles = user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toSet());

        return Jwts.builder()
                .subject(user.getUsername())
                .issuer(jwtProperties.getIssuer())
                .issuedAt(now)
                .expiration(expiry)
                .claim("userId", user.getId())
                .claim("email", user.getEmail())
                .claim("roles", roles)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Generate a refresh token (longer-lived, minimal claims).
     */
    public String generateRefreshToken(User user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtProperties.getRefreshExpirationMs());

        return Jwts.builder()
                .subject(user.getUsername())
                .issuer(jwtProperties.getIssuer())
                .issuedAt(now)
                .expiration(expiry)
                .claim("userId", user.getId())
                .claim("type", "refresh")
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extract username (subject) from a token.
     */
    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Extract user ID from a token.
     */
    public Long getUserIdFromToken(String token) {
        return parseClaims(token).get("userId", Long.class);
    }

    /**
     * Validate a JWT token.
     *
     * @return true if the token is valid and not expired
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException ex) {
            log.warn("JWT token expired: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.warn("Invalid JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.warn("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.warn("JWT claims string is empty: {}", ex.getMessage());
        } catch (JwtException ex) {
            log.warn("JWT validation failed: {}", ex.getMessage());
        }
        return false;
    }

    /**
     * Get access token expiration in seconds.
     */
    public long getExpirationSeconds() {
        return jwtProperties.getExpirationMs() / 1000;
    }

    /**
     * Get refresh token expiration in milliseconds.
     */
    public long getRefreshExpirationMs() {
        return jwtProperties.getRefreshExpirationMs();
    }

    // ── Private helpers ─────────────────────────────────────────

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        // If the key looks Base64-encoded, decode it; otherwise use raw bytes
        String secret = jwtProperties.getSecretKey();
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secret);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            return Keys.hmacShaKeyFor(secret.getBytes());
        }
    }
}
