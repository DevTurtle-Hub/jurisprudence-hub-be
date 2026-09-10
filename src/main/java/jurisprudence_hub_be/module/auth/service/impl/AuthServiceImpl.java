package jurisprudence_hub_be.module.auth.service.impl;

import jurisprudence_hub_be.common.constant.ErrorCode;
import jurisprudence_hub_be.common.constant.SecurityConstants;
import jurisprudence_hub_be.common.exception.BusinessException;
import jurisprudence_hub_be.common.exception.ForbiddenException;
import jurisprudence_hub_be.common.exception.ResourceNotFoundException;
import jurisprudence_hub_be.common.exception.UnauthorizedException;
import jurisprudence_hub_be.common.security.JwtService;
import jurisprudence_hub_be.common.security.UserPrincipal;
import jurisprudence_hub_be.common.security.util.SecurityUtil;
import jurisprudence_hub_be.module.auth.constant.AuthConstant;
import jurisprudence_hub_be.module.auth.dto.request.LoginRequest;
import jurisprudence_hub_be.module.auth.dto.request.RefreshTokenRequest;
import jurisprudence_hub_be.module.auth.dto.request.RegisterRequest;
import jurisprudence_hub_be.module.auth.dto.response.AuthResponse;
import jurisprudence_hub_be.module.auth.dto.response.TokenResponse;
import jurisprudence_hub_be.module.auth.dto.response.UserProfileResponse;
import jurisprudence_hub_be.module.auth.dto.response.UserResponse;
import jurisprudence_hub_be.module.auth.entity.RefreshToken;
import jurisprudence_hub_be.module.auth.entity.User;
import jurisprudence_hub_be.module.auth.enums.Role;
import jurisprudence_hub_be.module.auth.repository.RefreshTokenRepository;
import jurisprudence_hub_be.module.auth.repository.UserRepository;
import jurisprudence_hub_be.module.auth.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (request == null || request.email() == null || request.email().isBlank()) {
            throw new BusinessException(ErrorCode.VALIDATION_FAILED, AuthConstant.MSG_INVALID_CREDENTIALS);
        }
        String normalizedEmail = request.email().toLowerCase(Locale.ROOT).trim();
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS, AuthConstant.MSG_EMAIL_ALREADY_EXISTS);
        }

        String userId = AuthConstant.USER_ID_PREFIX + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        String name = request.name() != null ? request.name().trim() : "";
        String rawPassword = request.password() != null ? request.password() : "";
        User user = User.builder()
                .id(userId)
                .name(name)
                .email(normalizedEmail)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .role(Role.USER)
                .unit(request.unit() != null ? request.unit().trim() : null)
                .active(true)
                .build();

        @SuppressWarnings("null")
        User savedUser = userRepository.save(user);

        UserPrincipal principal = UserPrincipal.create(savedUser);
        String accessToken = jwtService.generateAccessToken(principal);
        String refreshToken = jwtService.generateRefreshToken(principal);

        saveRefreshToken(savedUser, refreshToken);

        UserResponse userResponse = new UserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole().name(),
                savedUser.getUnit(),
                savedUser.getCreatedAt(),
                null);

        TokenResponse tokenResponse = TokenResponse.of(
                accessToken,
                refreshToken,
                SecurityConstants.ACCESS_TOKEN_EXPIRATION);

        return new AuthResponse(userResponse, tokenResponse);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        if (request == null || request.email() == null || request.password() == null) {
            throw new UnauthorizedException(AuthConstant.MSG_INVALID_CREDENTIALS);
        }
        String normalizedEmail = request.email().toLowerCase(Locale.ROOT).trim();
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new UnauthorizedException(AuthConstant.MSG_INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new UnauthorizedException(AuthConstant.MSG_INVALID_CREDENTIALS);
        }

        if (!user.isActive()) {
            throw new ForbiddenException(AuthConstant.MSG_ACCOUNT_DISABLED);
        }

        UserPrincipal principal = UserPrincipal.create(user);
        String accessToken = jwtService.generateAccessToken(principal);
        String refreshToken = jwtService.generateRefreshToken(principal);

        saveRefreshToken(user, refreshToken);

        UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                user.getUnit(),
                null,
                Instant.now());

        TokenResponse tokenResponse = TokenResponse.of(
                accessToken,
                refreshToken,
                SecurityConstants.ACCESS_TOKEN_EXPIRATION);

        return new AuthResponse(userResponse, tokenResponse);
    }

    @Override
    @Transactional(readOnly = true)
    @SuppressWarnings("null")
    public UserProfileResponse getMe() {
        String currentUserId = SecurityUtil.requireCurrentUserId();
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException(AuthConstant.MSG_USER_NOT_FOUND));

        return new UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole().name(),
                user.getUnit(),
                user.getAvatarUrl(),
                user.isActive(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }

    @Override
    @Transactional
    public TokenResponse refreshToken(RefreshTokenRequest request) {
        if (request == null || request.refreshToken() == null || request.refreshToken().isBlank()) {
            throw new UnauthorizedException(AuthConstant.MSG_REFRESH_TOKEN_MISSING);
        }
        String tokenString = request.refreshToken();

        RefreshToken storedToken = refreshTokenRepository.findByToken(tokenString)
                .orElseThrow(() -> new BusinessException(ErrorCode.TOKEN_INVALID,
                        AuthConstant.MSG_REFRESH_TOKEN_NOT_FOUND_OR_EXPIRED));

        if (storedToken.getExpiresAt() == null || storedToken.getExpiresAt().isBefore(Instant.now())) {
            refreshTokenRepository.delete(storedToken);
            throw new BusinessException(ErrorCode.TOKEN_EXPIRED, AuthConstant.MSG_REFRESH_TOKEN_EXPIRED);
        }

        jwtService.validateRefreshToken(tokenString);
        User user = storedToken.getUser();
        if (user == null) {
            throw new UnauthorizedException(AuthConstant.MSG_USER_NOT_FOUND);
        }
        if (!user.isActive()) {
            throw new ForbiddenException(AuthConstant.MSG_ACCOUNT_DISABLED);
        }

        UserPrincipal principal = UserPrincipal.create(user);
        String newAccessToken = jwtService.generateAccessToken(principal);

        return TokenResponse.accessOnly(newAccessToken, SecurityConstants.ACCESS_TOKEN_EXPIRATION);
    }

    @Override
    @Transactional
    public void logout(RefreshTokenRequest request) {
        if (request != null && request.refreshToken() != null && !request.refreshToken().isBlank()) {
            String tokenString = request.refreshToken();
            refreshTokenRepository.deleteByToken(tokenString);
        }
    }

    @SuppressWarnings("null")
    private void saveRefreshToken(User user, String tokenString) {
        if (user == null || tokenString == null) return;
        RefreshToken token = RefreshToken.builder()
                .user(user)
                .token(tokenString)
                .expiresAt(Instant.now().plusSeconds(SecurityConstants.REFRESH_TOKEN_EXPIRATION))
                .build();
        refreshTokenRepository.save(token);
    }
}
