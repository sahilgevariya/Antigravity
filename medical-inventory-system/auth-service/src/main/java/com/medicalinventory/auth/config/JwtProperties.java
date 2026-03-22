package com.medicalinventory.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * JWT configuration properties.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /** HMAC secret key for signing tokens (used in dev; RS256 in prod) */
    private String secretKey = "medical-inventory-super-secret-key-that-is-at-least-256-bits-long-for-hs256";

    /** Access token expiration in milliseconds (default: 1 hour) */
    private long expirationMs = 3600000;

    /** Refresh token expiration in milliseconds (default: 24 hours) */
    private long refreshExpirationMs = 86400000;

    /** Token issuer */
    private String issuer = "medical-inventory-auth-service";
}
