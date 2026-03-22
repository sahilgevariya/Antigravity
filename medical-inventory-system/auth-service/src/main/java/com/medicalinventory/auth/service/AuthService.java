package com.medicalinventory.auth.service;

import com.medicalinventory.auth.dto.*;

/**
 * Authentication service contract.
 */
public interface AuthService {

    /**
     * Register a new user.
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Authenticate a user with username/password and return JWT tokens.
     */
    AuthResponse login(LoginRequest request);

    /**
     * Refresh an expired access token using a valid refresh token.
     */
    AuthResponse refreshToken(TokenRefreshRequest request);

    /**
     * Logout: revoke all refresh tokens for the user.
     */
    void logout(String username);
}
