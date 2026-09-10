package jurisprudence_hub_be.common.constant;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // =========================
    // SYSTEM
    // =========================
    INTERNAL_SERVER_ERROR("SYS_001", "Internal server error"),
    INVALID_REQUEST("SYS_002", "Invalid request"),
    RESOURCE_NOT_FOUND("SYS_003", "Resource not found"),
    METHOD_NOT_ALLOWED("SYS_004", "Method not allowed"),
    UNSUPPORTED_MEDIA_TYPE("SYS_005", "Unsupported media type"),
    RATE_LIMIT_EXCEEDED("SYS_006", "Rate limit exceeded"),

    // =========================
    // AUTHENTICATION
    // =========================
    UNAUTHORIZED("AUTH_001", "Authentication required"),
    INVALID_CREDENTIALS("AUTH_002", "Invalid credentials"),
    ACCESS_DENIED("AUTH_003", "Access denied"),
    TOKEN_INVALID("AUTH_004", "Invalid token"),
    TOKEN_EXPIRED("AUTH_005", "Token expired"),
    TOKEN_MISSING("AUTH_006", "Token missing"),
    INVALID_TOKEN_FORMAT("AUTH_007", "Invalid token format"),

    // =========================
    // USER
    // =========================
    USER_NOT_FOUND("USER_001", "User not found"),
    EMAIL_ALREADY_EXISTS("USER_002", "Email already exists"),
    USER_DISABLED("USER_003", "User is disabled"),
    USER_LOCKED("USER_004", "User account is locked"),
    INVALID_EMAIL("USER_005", "Invalid email format"),
    WEAK_PASSWORD("USER_006", "Password does not meet security requirements"),
    PASSWORD_MISMATCH("USER_007", "Passwords do not match"),

    // =========================
    // VALIDATION
    // =========================
    VALIDATION_FAILED("VAL_001", "Validation failed"),
    INVALID_FIELD("VAL_002", "Invalid field value"),
    REQUIRED_FIELD_MISSING("VAL_003", "Required field is missing"),
    INVALID_FORMAT("VAL_004", "Invalid format"),
    OUT_OF_RANGE("VAL_005", "Value out of allowed range"),

    // =========================
    // BUSINESS
    // =========================
    CONFLICT("BIZ_001", "Resource conflict"),
    OPERATION_NOT_ALLOWED("BIZ_002", "Operation not allowed"),
    INSUFFICIENT_PERMISSIONS("BIZ_003", "Insufficient permissions"),
    QUOTA_EXCEEDED("BIZ_004", "Quota exceeded"),
    DEPENDENCY_NOT_FOUND("BIZ_005", "Required dependency not found");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getPrefix() {
        return code.substring(0, code.indexOf('_'));
    }

    public int getNumber() {
        return Integer.parseInt(code.substring(code.indexOf('_') + 1));
    }
}
