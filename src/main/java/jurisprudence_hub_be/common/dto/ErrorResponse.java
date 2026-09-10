package jurisprudence_hub_be.common.dto;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
        boolean success,
        String code,
        String message,
        String path,
        Instant timestamp,
        String requestId,
        Map<String, String> errors,
        String details
) {

    // Backward compatible constructors
    public ErrorResponse(boolean success, String code, String message, String path, Instant timestamp, Map<String, String> errors) {
        this(success, code, message, path, timestamp, null, errors, null);
    }

    // Original methods for backward compatibility
    public static ErrorResponse of(String code, String message, String path) {
        return new ErrorResponse(false, code, message, path, Instant.now(), null);
    }

    // Enhanced methods with additional metadata
    public static ErrorResponse of(String code, String message, String path, String requestId) {
        return new ErrorResponse(false, code, message, path, Instant.now(), requestId, null, null);
    }

    public static ErrorResponse validation(String code, String message, String path, Map<String, String> errors) {
        return new ErrorResponse(false, code, message, path, Instant.now(), errors);
    }

    public static ErrorResponse validation(String code, String message, String path, Map<String, String> errors, String requestId) {
        return new ErrorResponse(false, code, message, path, Instant.now(), requestId, errors, null);
    }

    public static ErrorResponse withDetails(String code, String message, String path, String details) {
        return new ErrorResponse(false, code, message, path, Instant.now(), null, null, details);
    }

    public static ErrorResponse withDetails(String code, String message, String path, String details, String requestId) {
        return new ErrorResponse(false, code, message, path, Instant.now(), requestId, null, details);
    }
}
