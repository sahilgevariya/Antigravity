package com.medicalinventory.auth.service.impl;

import com.medicalinventory.auth.domain.RefreshToken;
import com.medicalinventory.auth.domain.Role;
import com.medicalinventory.auth.domain.User;
import com.medicalinventory.auth.dto.*;
import com.medicalinventory.auth.mapper.UserMapper;
import com.medicalinventory.auth.repository.RefreshTokenRepository;
import com.medicalinventory.auth.repository.RoleRepository;
import com.medicalinventory.auth.repository.UserRepository;
import com.medicalinventory.auth.service.AuthService;
import com.medicalinventory.auth.util.JwtTokenProvider;
import com.medicalinventory.common.exception.BusinessRuleException;
import com.medicalinventory.common.exception.DuplicateResourceException;
import com.medicalinventory.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Implementation of {@link AuthService} handling registration, login,
 * token refresh, and logout.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check for duplicates
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("User", "username", request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        // Resolve role (default to PHARMACIST)
        String roleName = (request.getRole() != null && !request.getRole().isBlank())
                ? request.getRole().toUpperCase()
                : "PHARMACIST";
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleName));

        // Build user entity
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .active(true)
                .ssoUser(false)
                .build();
        user.addRole(role);

        user = userRepository.save(user);
        log.info("User registered: {} with role {}", user.getUsername(), roleName);

        return generateTokenResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessRuleException("Invalid username or password"));

        if (!user.isActive()) {
            throw new BusinessRuleException("Account is deactivated. Contact administrator.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessRuleException("Invalid username or password");
        }

        log.info("User logged in: {}", user.getUsername());
        return generateTokenResponse(user);
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(TokenRefreshRequest request) {
        RefreshToken refreshToken = refreshTokenRepository
                .findByTokenAndRevokedFalse(request.getRefreshToken())
                .orElseThrow(() -> new BusinessRuleException("Invalid or revoked refresh token"));

        if (refreshToken.isExpired()) {
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);
            throw new BusinessRuleException("Refresh token has expired. Please login again.");
        }

        // Revoke the old refresh token (rotation)
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        User user = refreshToken.getUser();
        log.info("Token refreshed for user: {}", user.getUsername());

        return generateTokenResponse(user);
    }

    @Override
    @Transactional
    public void logout(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        refreshTokenRepository.revokeAllByUserId(user.getId());
        log.info("User logged out (all refresh tokens revoked): {}", username);
    }

    // ── Private helpers ─────────────────────────────────────────

    private AuthResponse generateTokenResponse(User user) {
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshTokenStr = jwtTokenProvider.generateRefreshToken(user);

        // Persist refresh token
        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenStr)
                .user(user)
                .expiresAt(LocalDateTime.now().plusSeconds(
                        jwtTokenProvider.getRefreshExpirationMs() / 1000))
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshToken);

        return AuthResponse.of(
                accessToken,
                refreshTokenStr,
                jwtTokenProvider.getExpirationSeconds(),
                userMapper.toResponse(user)
        );
    }
}
