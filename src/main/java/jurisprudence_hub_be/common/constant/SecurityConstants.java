package jurisprudence_hub_be.common.constant;

public final class SecurityConstants {

    private SecurityConstants() {
    }

    // Public endpoints that don't require authentication
    public static final String[] PUBLIC_ENDPOINTS = {
            "/api/v1/auth/register",
            "/api/v1/auth/login",
            "/api/v1/auth/refresh-token",
            "/api/v1/auth/logout",
            "/api/v1/public/**",
            "/actuator/**",
            "/error",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };

    // Header & Token Prefix
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";

    // JWT Claims & Types
    public static final String TOKEN_TYPE_ACCESS = "access";
    public static final String TOKEN_TYPE_REFRESH = "refresh";
    public static final String CLAIM_TOKEN_TYPE = "tokenType";
    public static final String JWT_USER_ID_CLAIM = "userId";
    public static final String JWT_EMAIL_CLAIM = "email";
    public static final String JWT_ROLES_CLAIM = "roles";
    public static final String CLAIM_NAME = "name";
    public static final String CLAIM_ROLE = "role";
    public static final String CLAIM_UNIT = "unit";

    // Role Configuration
    public static final String ROLE_PREFIX = "ROLE_";
    public static final String DEFAULT_ROLE = "USER";
    public static final String ADMIN_ROLE = "ADMIN";

    // Token expiration times (in seconds)
    public static final long ACCESS_TOKEN_EXPIRATION = 15 * 60; // 15 minutes
    public static final long REFRESH_TOKEN_EXPIRATION = 7 * 24 * 60 * 60; // 7 days

    // Security & JWT Messages
    public static final String MSG_TOKEN_MISSING = "Refresh token không được để trống";
    public static final String MSG_TOKEN_NOT_REFRESH = "Token không phải là refresh token";
    public static final String MSG_TOKEN_EXPIRED = "Token đã hết hạn";
    public static final String MSG_TOKEN_INVALID = "Token không hợp lệ";
    public static final String MSG_REFRESH_TOKEN_EXPIRED = "Refresh token đã hết hạn";
    public static final String MSG_REFRESH_TOKEN_INVALID = "Refresh token không hợp lệ: ";
    public static final String MSG_USER_NOT_FOUND_BY_EMAIL = "Không tìm thấy người dùng với email: ";
    public static final String MSG_AUTH_FAILED_LOG = "Không thể xác thực token JWT: {}";
}
