package jurisprudence_hub_be.common.dto;

import jurisprudence_hub_be.common.constant.AppConstants;

import java.time.Instant;

public record ApiResponse<T>(
        boolean success,
        Integer statusCode,
        String message,
        T data,
        Instant timestamp,
        String requestId,
        String path
) {

    public ApiResponse(boolean success, String message, T data, Instant timestamp) {
        this(success, success ? 200 : 400, message, data, timestamp, null, null);
    }

    public ApiResponse(boolean success, String message, T data, Instant timestamp, String requestId) {
        this(success, success ? 200 : 400, message, data, timestamp, requestId, null);
    }

    public static <T> ApiResponse<T> of(boolean success, int statusCode, String message, T data) {
        return new ApiResponse<>(success, statusCode, message, data, Instant.now(), null, null);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, 200, AppConstants.SUCCESS_MESSAGE, data, Instant.now(), null, null);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, 200, message, data, Instant.now(), null, null);
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, 200, message, null, Instant.now(), null, null);
    }

    public static <T> ApiResponse<T> success(String message, T data, String requestId) {
        return new ApiResponse<>(true, 200, message, data, Instant.now(), requestId, null);
    }

    public static <T> ApiResponse<T> success(String message, T data, String requestId, String path) {
        return new ApiResponse<>(true, 200, message, data, Instant.now(), requestId, path);
    }

    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(true, 201, AppConstants.CREATED_MESSAGE, data, Instant.now(), null, null);
    }

    public static <T> ApiResponse<T> created(String message, T data) {
        return new ApiResponse<>(true, 201, message, data, Instant.now(), null, null);
    }

    public static <T> ApiResponse<T> created(T data, String requestId) {
        return new ApiResponse<>(true, 201, AppConstants.CREATED_MESSAGE, data, Instant.now(), requestId, null);
    }

    public static <T> ApiResponse<T> updated(T data) {
        return new ApiResponse<>(true, 200, AppConstants.UPDATED_MESSAGE, data, Instant.now(), null, null);
    }

    public static <T> ApiResponse<T> updated(String message, T data) {
        return new ApiResponse<>(true, 200, message, data, Instant.now(), null, null);
    }

    public static <T> ApiResponse<T> updated(T data, String requestId) {
        return new ApiResponse<>(true, 200, AppConstants.UPDATED_MESSAGE, data, Instant.now(), requestId, null);
    }

    public static <T> ApiResponse<T> deleted() {
        return new ApiResponse<>(true, 200, AppConstants.DELETED_MESSAGE, null, Instant.now(), null, null);
    }

    public static <T> ApiResponse<T> deleted(String message) {
        return new ApiResponse<>(true, 200, message, null, Instant.now(), null, null);
    }

    public static <T> ApiResponse<T> deleted(String message, String requestId) {
        return new ApiResponse<>(true, 200, message, null, Instant.now(), requestId, null);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, 400, message, null, Instant.now(), null, null);
    }

    public static <T> ApiResponse<T> error(int statusCode, String message) {
        return new ApiResponse<>(false, statusCode, message, null, Instant.now(), null, null);
    }

    public static <T> ApiResponse<T> error(String message, String requestId) {
        return new ApiResponse<>(false, 400, message, null, Instant.now(), requestId, null);
    }
}
